package com.v2soft.productrating.services.converters;

import com.v2soft.productrating.services.dtos.ReviewDTO;
import com.v2soft.productrating.domain.Review;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class ReviewToReviewDTO implements Converter<Review, ReviewDTO> {
    //try catch or logs for missing bean
    @Override
    public ReviewDTO convert(Review source) {
        ReviewDTO reviewDTO = new ReviewDTO();

        reviewDTO.setRatingId(source.getRatingId());
        reviewDTO.setUserId(source.getUserId());
        reviewDTO.setFirstName(source.getFirstName());
        reviewDTO.setLastName(source.getLastName());
        reviewDTO.setProduct(source.getProduct());
        reviewDTO.setZipcode(source.getZipcode());
        reviewDTO.setEmail(source.getEmail());
        reviewDTO.setScore(source.getScore());
        reviewDTO.setComment(source.getComment());
        reviewDTO.setImageDetailsList(source.getImageDetailsList());
        reviewDTO.setDateAndTime(source.getDateAndTime());

        return reviewDTO;
    }
}
