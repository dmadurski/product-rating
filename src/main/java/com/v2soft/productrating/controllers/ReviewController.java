package com.v2soft.productrating.controllers;

import com.v2soft.productrating.domain.ImageDetails;
import com.v2soft.productrating.domain.Review;
import com.v2soft.productrating.services.AuthorizationService;
import com.v2soft.productrating.services.UserService;
import com.v2soft.productrating.services.dtos.LoginRequestDTO;
import com.v2soft.productrating.services.dtos.UserDTO;
import com.v2soft.productrating.services.dtos.ReviewDTO;

import com.v2soft.productrating.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class ReviewController {

    private static final Logger infoAndDebuglogger = LogManager.getLogger("InfoAndDebugLogger");
    private static final Logger metricsLogger = LogManager.getLogger("MetricsLogger");

    final ReviewService reviewService;
    final UserService userService;
    final AuthorizationService authorizationService;

    @PreAuthorize("@authorizationServiceImpl.verifyGenericToken(#token)")
    @GetMapping("/allReviews")
    public List<ReviewDTO> showAllReviews(@RequestHeader(name="Authorization") String token){
        metricsLogger.info("Accessed /allReviews endpoint");

        return reviewService.findAll();
    }

    @GetMapping("/lastTenReviews")
    public List<ReviewDTO> showLastTenReviews(){
        metricsLogger.info("Accessed /lastTenReviews endpoint");

        return reviewService.findLastTenReviews();
    }

    @GetMapping("/lastTenReviewsCustom")
    public List<ReviewDTO> showLastTenReviewsCustom(){
        metricsLogger.info("Accessed /lastTenReviews endpoint");

        return reviewService.findLastTenReviewsCustom();
    }

    @GetMapping("/specificReview")
    public ResponseEntity<Object> showSpecificReview(@RequestBody String reviewCode){
        metricsLogger.info("Accessed /specificReview endpoint");
        return reviewService.findReviewByReviewCode(reviewCode);
    }

    @GetMapping("/productReviews")
    public ResponseEntity<Object> showProductReviews(@RequestBody String productName){
        metricsLogger.info("Accessed /productReviews endpoint");

        return reviewService.findReviewsByProductName(productName);
    }

    @GetMapping("/productReviewsCustom")
    public ResponseEntity<Object> showProductReviewsCustom(@RequestBody String productName){
        metricsLogger.info("Accessed /productReviews endpoint");

        return reviewService.findReviewsByProductNameCustom(productName);
    }

    @PreAuthorize("@authorizationServiceImpl.verifyGenericToken(#token)")
    @PostMapping("/newReview")
    public ResponseEntity<Object> newReview(@RequestHeader(name="Authorization") String token,
                                            @RequestParam("userId") String userId,
                                            @RequestParam("firstName") String firstName,
                                            @RequestParam("lastName") String lastName,
                                            @RequestParam("zipcode") int zipcode,
                                            @RequestParam("product") String product,
                                            @RequestParam("email") String email,
                                            @RequestParam("score") int score,
                                            @RequestParam("comment") String comment,
                                            @RequestParam(value = "imageDetailsList", required = false) String imageDetailsListJson) {
        System.out.println("It reaches inside the new review controller method");
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // Parse the JSON array into a list of ImageDetails
            List<ImageDetails> imageDetailsList = new ArrayList<ImageDetails>();
            if(imageDetailsListJson != null) {
                System.out.println("ImageDetailsList from angular is:");
                System.out.println(imageDetailsListJson);
                imageDetailsList = objectMapper.readValue(
                        imageDetailsListJson,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, ImageDetails.class)
                );
            }

            // Create a ReviewDTO object with the provided parameters
            ReviewDTO newReviewDTO = new ReviewDTO();
            newReviewDTO.setUserId(userId);
            newReviewDTO.setFirstName(firstName);
            newReviewDTO.setLastName(lastName);
            newReviewDTO.setZipcode(zipcode);
            newReviewDTO.setProduct(product);
            newReviewDTO.setEmail(email);
            newReviewDTO.setScore(score);
            newReviewDTO.setComment(comment);
            newReviewDTO.setImageDetailsList(imageDetailsList);

            return reviewService.saveReview(newReviewDTO);
        } catch (IOException e) {
            // Handle JSON exception
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("@authorizationServiceImpl.verifyToken(#token, #userId, 'delete')")
    @DeleteMapping("/deleteAll")
    public ResponseEntity<Object> deleteReviews(@RequestHeader(name="Authorization") String token,
                                                @RequestHeader(name = "UserId") String userId){
        metricsLogger.info("Accessed /deleteAll endpoint");

        reviewService.deleteAllReviews();
        return ResponseEntity.ok("All reviews deleted!");
    }

    @PreAuthorize("@authorizationServiceImpl.verifyToken(#token, #userId, 'delete')")
    @DeleteMapping("/backendDeleteReview")
    public ResponseEntity<Object> backendDeleteReview(@RequestHeader(name = "Authorization") String token,
                                                      @RequestHeader(name = "UserId") String userId, @RequestBody String reviewId){
        metricsLogger.info("Accessed /backendDeleteReview endpoint");

        return reviewService.deleteSpecificReview(reviewId);
    }

    @PreAuthorize("@authorizationServiceImpl.verifyGenericToken(#token)")
    @DeleteMapping("/deleteReview")
    public ResponseEntity<Object> deleteReview(@RequestHeader(name = "Authorization") String token, @RequestBody String reviewId){
        metricsLogger.info("Accessed /deleteReview endpoint");
        ResponseEntity<Object> response = reviewService.deleteSpecificReview(reviewId);
        if(response.getStatusCode().equals(HttpStatus.OK)){
            return ResponseEntity.ok("Review Deleted");
        } else {
            return response;
        }
    }

    //add new authorization with a new secretKey
    @PreAuthorize("@authorizationServiceImpl.verifyToken(#token, #userId, 'update')")
    @PostMapping("/updateReview")
    public ResponseEntity<Object> updateReview(@RequestHeader(name = "Authorization") String token,
                                               @RequestHeader(name = "UserId") String userId, @RequestBody Review updatedReview) {
        metricsLogger.info("Accessed /updateReview endpoint");

        return reviewService.updateReview(updatedReview);
    }

    @GetMapping("/findReview")
    public Review findReview(@RequestBody String id) {

        return reviewService.findReview(id);
    }

    @PreAuthorize("@authorizationServiceImpl.verifyGenericToken(#token)")
    @GetMapping("/findReviewsByOwnerId")
    public List<ReviewDTO> findReviewsByOwnerId(@RequestHeader(name="Authorization") String token, @RequestParam String ownerId) {

        return reviewService.findReviewByOwnerId(ownerId);
    }

    @PostMapping("/newUser")
    public ResponseEntity<Object> newUser(@RequestBody UserDTO userDTO) {
        try {
            ResponseEntity<Object> registrationResponse = userService.createNewUser(userDTO);
            if (registrationResponse.getStatusCode() == HttpStatus.OK) {
                LoginRequestDTO loginRequestDTO = (LoginRequestDTO) registrationResponse.getBody();
                assert loginRequestDTO != null; //If the status code of the response is OK, then this cannot be null. Bypass the warning.
                return reviewService.userLogin(loginRequestDTO.getUserName(), loginRequestDTO.getPassword());
            } else {
                return registrationResponse;
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> userLogin(@RequestBody LoginRequestDTO loginRequestDTO) {
        return reviewService.userLogin(loginRequestDTO.getUserName(), loginRequestDTO.getPassword());
    }

    @GetMapping("/checkJwt")
    public boolean checkJwt(@RequestBody String jwtString) {
        return authorizationService.verifyGenericToken(jwtString);
    }
}