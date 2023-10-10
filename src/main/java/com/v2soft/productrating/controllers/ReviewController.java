package com.v2soft.productrating.controllers;

import com.v2soft.productrating.domain.Review;
import com.v2soft.productrating.repositories.TokenRepository;
import com.v2soft.productrating.services.AuthorizationService;
import com.v2soft.productrating.services.dtos.ReviewDTO;

import com.v2soft.productrating.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private static final Logger metricsLogger = LogManager.getLogger("MetricsLogger");

    final ReviewService reviewService;
    final TokenRepository tokenRepository;
    final AuthorizationService authorizationService;

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

    @PostMapping("/newReview")
    public ResponseEntity<Object> newReview(@RequestBody ReviewDTO newReviewDTO){
        metricsLogger.info("Accessed /newReview endpoint");

        return reviewService.saveReview(newReviewDTO);
    }

    @GetMapping("/deleteAll")
    public ResponseEntity<Object> deleteReviews(@RequestHeader(name="Authorization") String token){
        if(authorizationService.verifyToken(token)){
            metricsLogger.info("Accessed /deleteAll endpoint");

            reviewService.deleteAllReviews();
            return ResponseEntity.ok("All reviews deleted!");
        } else {
            return new ResponseEntity<>("Authorization Denied", HttpStatus.UNAUTHORIZED) ;
        }
    }

    @GetMapping("/deleteReview")
    public ResponseEntity<Object> deleteReview(@RequestBody String id,
                                               @RequestHeader(name="Authorization") String token){

        if(authorizationService.verifyToken(token)){
            metricsLogger.info("Accessed /deleteReview endpoint");

            return reviewService.deleteSpecificReview(id);
        } else {
            return new ResponseEntity<>("Authorization Denied", HttpStatus.UNAUTHORIZED);
        }

    }

    @PostMapping("/updateReview")
    public ResponseEntity<Object> updateReview(@RequestBody Review updatedReview){
        metricsLogger.info("Accessed /updateReview endpoint");

        return reviewService.updateReview(updatedReview);
    }

    @GetMapping("/findReview")
    public Review findReview(@RequestBody String id){

        return reviewService.findReview(id);
    }

    @GetMapping("/generateJWTToken")
    public String generateJwt(){

        return reviewService.createJWT();
    }
}