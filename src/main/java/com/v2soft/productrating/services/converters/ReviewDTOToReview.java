package com.v2soft.productrating.services.converters;

import com.v2soft.productrating.services.dtos.ReviewDTO;
import com.v2soft.productrating.domain.Review;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.ArrayList;

@Service
public class ReviewDTOToReview implements Converter<ReviewDTO, Review> {

    @Override
    public Review convert(ReviewDTO source) {
        Review review = new Review();

        review.setRatingId(source.getRatingId());
        review.setUserId(source.getUserId());
        review.setFirstName(source.getFirstName());
        review.setLastName(source.getLastName());
        review.setProduct(source.getProduct());
        review.setZipcode(source.getZipcode());
        review.setEmail(source.getEmail());
        review.setScore(source.getScore());
        review.setComment(source.getComment());
        review.setImageDetailsList(source.getImageDetailsList());
        review.setDateAndTime(LocalDateTime.now());
        review.setPreviousVersions(new ArrayList<>());
        review.setVersionCount(1);

        return review;
    }
}