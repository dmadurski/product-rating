package com.v2soft.productrating.services;

import com.v2soft.productrating.domain.Token;
import com.v2soft.productrating.repositories.TokenRepository;
import com.v2soft.productrating.services.dtos.ReviewDTO;
import com.v2soft.productrating.domain.Review;
import com.v2soft.productrating.repositories.ReviewRepository;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @Value("${jwt.secret.key}")
    private String secret;

    final MongoTemplate mongoTemplate;
    final ReviewRepository reviewRepository;
    final TokenRepository tokenRepository;
    final ConversionService conversionService;

    @Override
    public List<ReviewDTO> findLastTenReviews() {

        List<Review> lastTenReviews = reviewRepository.findTop10ByOrderByDateAndTimeDesc();

        return lastTenReviews.stream()
                .map((review -> (conversionService.convert(review, ReviewDTO.class))))
                .toList();
    }

    @Override
    public List<ReviewDTO> findLastTenReviewsCustom() {
        Query query = new Query().with(Sort.by(Sort.Order.desc("dateAndTime"))).limit(sortLimit);
        List<Review> lastTenReviews = mongoTemplate.find(query, Review.class);

        return lastTenReviews.stream()
                .map((review -> (conversionService.convert(review, ReviewDTO.class))))
                .toList();
    }

    @Override
    public ResponseEntity<Object> findReviewByReviewCode(String reviewCode) {
        Optional<Review> reviewOptional = reviewRepository.findById(reviewCode);

        //If no review is found with the code, return an explanation message
        if (reviewOptional.isPresent()){
            ReviewDTO foundReviewDTO = conversionService.convert(reviewOptional.get(), ReviewDTO.class);
            return ResponseEntity.ok(foundReviewDTO);
        } else {
            infoAndDebuglogger.debug("Unable to find a review with reviewCode: " + reviewCode);
            return new ResponseEntity<>("Unable to find a review matching provided ID. Make sure ID is correct and try again.", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Object> findReviewsByProductName(String productName) {
        List<Review> reviewsForProduct = reviewRepository.findByProduct(productName);

        //Convert the list of Reviews to a list of ReviewDTOs
        List<ReviewDTO> reviewDTOSForProduct = reviewsForProduct.stream()
                .map((review -> (conversionService.convert(review, ReviewDTO.class))))
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
                .map((review -> (conversionService.convert(review, ReviewDTO.class))))
                .toList();

        //If no product reviews are found, return an explanation message
        if(!reviewDTOSForProduct.isEmpty()){
            return ResponseEntity.ok(reviewDTOSForProduct);
        } else {
            infoAndDebuglogger.debug("Unable to find any reviews for product: " + productName);
            return new ResponseEntity<>("Unable to find any reviews for that product.", HttpStatus.NOT_FOUND);
        }
    }

    //Ignore property for optional properites in domain (for example: comments)
    @Override
    public ResponseEntity<Object> saveReview(ReviewDTO newReviewDTO) {
        Review newReview = conversionService.convert(newReviewDTO, Review.class);

        //This code can be removed once input validation is included on the front-end
        if(newReview == null){
            infoAndDebuglogger.debug("Unable to convert following ReviewDTO to a Review: " + newReviewDTO);
            return new ResponseEntity<>("Save request unsuccessful. " +
                    "Make sure all fields are correct and try again.", HttpStatus.BAD_REQUEST);
        }

        reviewRepository.save(newReview);
        Optional<Review> reviewOptional = reviewRepository.findById(newReview.getRatingCode());

        //Check if the save was successful, if so, return the DTO, otherwise explain the error
        if (reviewOptional.isPresent()){
            infoAndDebuglogger.info("Saved new review: " + newReview);
            //Add the review to the log if the score is low
            if(newReview.getScore() < poorScore) lowReviewsLogger.info("Bad review:" + newReview.reviewCollectionToString());
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
            reviewRepository.deleteById(reviewToDelete.getRatingCode());
            infoAndDebuglogger.info("Review deleted");
            return ResponseEntity.ok(reviewRepository.findAll());
        } else {
            infoAndDebuglogger.debug("No review found with provided id: " + id);
            return new ResponseEntity<>("Unable to find a review with provided ID. Please try again.", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Object> updateReview(Review updatedReview) {
        Optional<Review> previousReviewOptional = reviewRepository.findById(updatedReview.getRatingCode());
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

            ReviewDTO updatedReviewDTO = conversionService.convert(updatedReview, ReviewDTO.class);
            return ResponseEntity.ok(updatedReviewDTO);
        } else {
            infoAndDebuglogger.debug("No review found with provided ID: " + updatedReview.getRatingCode());
            return new ResponseEntity<>("Unable to find a review with provided ID. Please try again.", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public String createJWT() {
        Date expiration = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
        byte[] secretKeyBytes = Base64.getDecoder().decode(secret);
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, "HmacSHA256");

        String jwtID = java.util.UUID.randomUUID().toString();
        String jwtString = Jwts.builder()
                .expiration(expiration)
                .id(jwtID)
                .signWith(secretKey)
                .compact();
        Token newJwt = new Token(jwtID, jwtString);
        tokenRepository.save(newJwt);
        return "Token successfully saved!";
    }

    @Override
    public Review findReview(String id) {
        Optional<Review> foundReviewOptional = reviewRepository.findById(id);
        return foundReviewOptional.orElse(null);

    }
}
