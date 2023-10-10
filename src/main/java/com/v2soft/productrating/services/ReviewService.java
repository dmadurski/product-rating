package com.v2soft.productrating.services;

import com.v2soft.productrating.domain.Review;
import com.v2soft.productrating.services.dtos.ReviewDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReviewService {

    List<ReviewDTO> findLastTenReviews();

    List<ReviewDTO> findLastTenReviewsCustom();

    ResponseEntity<Object> findReviewByReviewCode(String reviewCode);

    ResponseEntity<Object> findReviewsByProductName(String productName);

    ResponseEntity<Object> findReviewsByProductNameCustom(String productName);

    ResponseEntity<Object> saveReview(ReviewDTO newReviewDTO);

    void deleteAllReviews();

    ResponseEntity<Object> deleteSpecificReview(String id);

    ResponseEntity<Object> updateReview(Review updatedReview);

    String createJWT();

    Review findReview(String id);
}
