package com.v2soft.productrating.controllers;

import com.v2soft.productrating.domain.Review;
import com.v2soft.productrating.services.AuthorizationService;
import com.v2soft.productrating.services.ClientService;
import com.v2soft.productrating.services.dtos.ReviewDTO;

import com.v2soft.productrating.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class ReviewController {

    private static final Logger metricsLogger = LogManager.getLogger("MetricsLogger");

    final ReviewService reviewService;
    final ClientService clientService;
    final AuthorizationService authorizationService;

    @GetMapping("/allReviews")
    public List<ReviewDTO> showAllReviews(){
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

    @PostMapping("/newReview")
    public ResponseEntity<Object> newReview(@RequestBody ReviewDTO newReviewDTO){
        metricsLogger.info("Accessed /newReview endpoint");

        return reviewService.saveReview(newReviewDTO);
    }

    @PreAuthorize("@authorizationServiceImpl.verifyToken(#token, #clientId, 'delete')")
    @GetMapping("/deleteAll")
    public ResponseEntity<Object> deleteReviews(@RequestHeader(name="Authorization") String token){
        metricsLogger.info("Accessed /deleteAll endpoint");

        reviewService.deleteAllReviews();
        return ResponseEntity.ok("All reviews deleted!");
    }

    @PreAuthorize("@authorizationServiceImpl.verifyToken(#token, #clientId, 'delete')")
    @GetMapping("/deleteReview")
    public ResponseEntity<Object> deleteReview(@RequestHeader(name = "Authorization") String token, @RequestHeader(name = "ClientId") String clientId, @RequestBody String id){
        metricsLogger.info("Accessed /deleteReview endpoint");

        return reviewService.deleteSpecificReview(id);
    }

    //add new authorization with a new secretKey
    @PreAuthorize("@authorizationServiceImpl.verifyToken(#token, #clientId, 'update')")
    @PostMapping("/updateReview")
    public ResponseEntity<Object> updateReview(@RequestHeader(name = "Authorization") String token, @RequestHeader(name = "ClientId") String clientId, @RequestBody Review updatedReview){
        metricsLogger.info("Accessed /updateReview endpoint");

        return reviewService.updateReview(updatedReview);
    }

    @GetMapping("/findReview")
    public Review findReview(@RequestBody String id){

        return reviewService.findReview(id);
    }

    @PostMapping("/newClient")
    public String newClient(@RequestBody String clientSecret){
        return clientService.createNewClient(clientSecret);
    }
}