package com.v2soft.productrating.services;

import com.v2soft.productrating.domain.ImageDetails;
import com.v2soft.productrating.repositories.UserRepository;
import com.v2soft.productrating.services.converters.ReviewDTOToReview;
import com.v2soft.productrating.services.converters.ReviewToReviewDTO;
import com.v2soft.productrating.services.dtos.ReviewDTO;
import com.v2soft.productrating.domain.Review;
import com.v2soft.productrating.repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService{

    private static final Logger infoAndDebuglogger = LogManager.getLogger("InfoAndDebugLogger");
    private static final Logger lowReviewsLogger = LogManager.getLogger("BadReviewLogger");

    @Value("${review.maximum.versions}")
    private int maximumVersionHistory;

    @Value("${review.poor.score}")
    private int poorScore;

    @Value("${review.sort.limit}")
    private int sortLimit;

    final MongoTemplate mongoTemplate;
    final ReviewRepository reviewRepository;
    final UserRepository userRepository;
    final ReviewDTOToReview reviewDTOToReviewConverter;
    final ReviewToReviewDTO reviewToReviewDTOConverter;
    final AuthorizationService authorizationService;
    final UserService userService;
    final TokenService tokenService;
    final ImageService imageService;

    @Override
    public List<ReviewDTO> findAll() {
        List<Review> allReviews = reviewRepository.findAll();

        return allReviews.stream()
                .map(reviewToReviewDTOConverter::convert)
                .toList();
    }

    @Override
    public List<ReviewDTO> findLastTenReviews() {

        List<Review> lastTenReviews = reviewRepository.findTop10ByOrderByDateAndTimeDesc();

        return lastTenReviews.stream()
                .map(reviewToReviewDTOConverter::convert)
                .toList();
    }

    @Override
    public List<ReviewDTO> findLastTenReviewsCustom() {
        Query query = new Query().with(Sort.by(Sort.Order.desc("dateAndTime"))).limit(sortLimit);
        List<Review> lastTenReviews = mongoTemplate.find(query, Review.class);

        return lastTenReviews.stream()
                .map(reviewToReviewDTOConverter::convert)
                .toList();
    }

    @Override
    public ResponseEntity<Object> findReviewByReviewCode(String reviewCode) {
        Optional<Review> reviewOptional = reviewRepository.findById(reviewCode);

        if(reviewOptional.isPresent()) {
            ReviewDTO foundReviewDTO = reviewToReviewDTOConverter.convert(reviewOptional.get());
            return ResponseEntity.ok(foundReviewDTO);
        } else {
            infoAndDebuglogger.debug("Unable to a review with code: " + reviewCode);
            return new ResponseEntity<>("Unable to find any reviews for that product.", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Object> findReviewsByProductName(String productName) {
        List<Review> reviewsForProduct = reviewRepository.findByProduct(productName);

        //Convert the list of Reviews to a list of ReviewDTOs
        List<ReviewDTO> reviewDTOSForProduct = reviewsForProduct.stream()
                .map(reviewToReviewDTOConverter::convert)
                .toList();

        //If no product reviews are found, return an explanation message
        if(!reviewDTOSForProduct.isEmpty()){
            return ResponseEntity.ok(reviewDTOSForProduct);
        } else {
            infoAndDebuglogger.debug("Unable to find any reviews for product: " + productName);
            return new ResponseEntity<>("Unable to find any reviews for that product.", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Object> findReviewsByProductNameCustom(String productName) {
        Query query = new Query().addCriteria(Criteria.where("product").is(productName));
        List<Review> reviewsForProduct = mongoTemplate.find(query, Review.class);

        //Convert the list of Reviews to a list of ReviewDTOs
        List<ReviewDTO> reviewDTOSForProduct = reviewsForProduct.stream()
                .map(reviewToReviewDTOConverter::convert)
                .toList();

        //If no product reviews are found, return an explanation message
        if(!reviewDTOSForProduct.isEmpty()){
            return ResponseEntity.ok(reviewDTOSForProduct);
        } else {
            infoAndDebuglogger.debug("Unable to find any reviews for product: " + productName);
            return new ResponseEntity<>("Unable to find any reviews for that product.", HttpStatus.NOT_FOUND);
        }
    }

    //Add a check to make sure the same user can't make multiple reviews for the same product
    @Override
    public ResponseEntity<Object> saveReview(ReviewDTO newReviewDTO) {
        Review newReview = reviewDTOToReviewConverter.convert(newReviewDTO);

        reviewRepository.save(newReview);
        Optional<Review> reviewOptional = reviewRepository.findById(newReview.getRatingId());

        //Check if the save was successful, if so, return the DTO, otherwise explain the error
        if (reviewOptional.isPresent()){
            infoAndDebuglogger.info("Saved new review: " + newReview);
            //Add the review to the log if the score is low
            if(newReview.getScore() < poorScore) lowReviewsLogger.info("Bad review:" + newReview.reviewCollectionToString());

            //Send an email confirming new review success to user
            userService.notifyReviewSuccess(reviewOptional.get());

            return ResponseEntity.ok(newReviewDTO);
        } else {
            infoAndDebuglogger.debug("Unable to save the following review: " + newReview);
            return new ResponseEntity<>("Save request unsuccessful. Please try again.", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void deleteAllReviews() {
        reviewRepository.deleteAll();

        infoAndDebuglogger.info("All reviews deleted");
    }

    @Override
    public ResponseEntity<Object> deleteSpecificReview(String id) {

        infoAndDebuglogger.info("Attempting to delete review with id: " + id);

        Optional<Review> reviewToDeleteOptional = reviewRepository.findById(id);
        if (reviewToDeleteOptional.isPresent()) {
            Review reviewToDelete = reviewToDeleteOptional.get();

            //If the review has images associated with it, delete those images from the image collection
            if(reviewToDelete.getImageDetailsList() != null && !reviewToDelete.getImageDetailsList().isEmpty()){
                List<ImageDetails> imageDetailsList = reviewToDelete.getImageDetailsList();
                for (ImageDetails imageDetails: imageDetailsList){
                    imageService.deleteImage(imageDetails.getImageId());
                }
            }

            reviewRepository.deleteById(reviewToDelete.getRatingId());
            infoAndDebuglogger.info("Review deleted");
            return ResponseEntity.ok(reviewRepository.findAll());
        } else {
            infoAndDebuglogger.debug("No review found with provided id: " + id);
            return new ResponseEntity<>("Unable to find a review with provided ID. Please try again.", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Object> updateReview(Review updatedReview) {
        Optional<Review> previousReviewOptional = reviewRepository.findById(updatedReview.getRatingId());
        if (previousReviewOptional.isPresent()){
            Review previousReview = previousReviewOptional.get();

            //Increment the version number, then if the maximum version history is reached, remove the oldest and add the recently changed version
            int updatedVersionCount = previousReview.versionCount + 1;
            updatedReview.setVersionCount(updatedVersionCount);
            ArrayList<Review> previousVersions = new ArrayList<>(previousReview.getPreviousVersions());

            if(updatedVersionCount <= maximumVersionHistory){
                previousVersions.add(previousReview);
            } else {
                previousVersions.remove(0);
                previousVersions.add(previousReview);
            }
            updatedReview.setPreviousVersions(previousVersions);

            updatedReview.setDateAndTime(LocalDateTime.now());

            infoAndDebuglogger.info("Updating previous review values: " + previousReview + " to new values: " + updatedReview);
            reviewRepository.save(updatedReview);

            if(updatedReview.getScore() < poorScore) lowReviewsLogger.info("Bad review:" + updatedReview.reviewCollectionToString());

            ReviewDTO updatedReviewDTO = reviewToReviewDTOConverter.convert(updatedReview);
            return ResponseEntity.ok(updatedReviewDTO);
        } else {
            infoAndDebuglogger.debug("No review found with provided ID: " + updatedReview.getRatingId());
            return new ResponseEntity<>("Unable to find a review with provided ID. Please try again.", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public List<ReviewDTO> findReviewByOwnerId(String userId) {
        List<Review> userReviews = reviewRepository.findByUserId(userId);

        return userReviews.stream()
                .map(reviewToReviewDTOConverter::convert)
                .toList();
    }

    @Override
    public Review findReview(String id) {
        Optional<Review> foundReviewOptional = reviewRepository.findById(id);
        return foundReviewOptional.orElse(null);
    }

    @Override
    public ResponseEntity<Object> userLogin(String userName, String password) {
        //Verify the login is correct, if so, generate a jwt and pass it back to the front end
        boolean isLoginSuccessful = authorizationService.verifyLogin(userName, password);
        if(!isLoginSuccessful){
            return new ResponseEntity<>("Login unsuccessful, make sure username and password are correct.", HttpStatus.UNAUTHORIZED);
        } else {
            return tokenService.generateJWT(userName);
        }
    }
}
