package com.v2soft.productrating.repositories;

import com.v2soft.productrating.domain.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {

    List<Review> findTop10ByOrderByDateAndTimeDesc();

    List<Review> findByProduct(String productName);

}
