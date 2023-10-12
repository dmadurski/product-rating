package com.v2soft.productrating.services.converters;

import com.v2soft.productrating.services.dtos.ReviewDTO;
import com.v2soft.productrating.domain.Review;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class ReviewToReviewDTO implements Converter<Review, ReviewDTO> {

    @Override
    public ReviewDTO convert(Review source) {
        ReviewDTO reviewDTO = new ReviewDTO();

        reviewDTO.setRatingCode(source.getRatingCode());
        reviewDTO.setFirstName(source.getFirstName());
        reviewDTO.setLastName(source.getLastName());
        reviewDTO.setProduct(source.getProduct());
        reviewDTO.setZipcode(source.getZipcode());
        reviewDTO.setScore(source.getScore());
        reviewDTO.setComment(source.getComment());

        return reviewDTO;
    }
}
