package com.v2soft.productrating;

import com.v2soft.productrating.services.converters.ReviewDTOToReview;
import com.v2soft.productrating.services.converters.ReviewToReviewDTO;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

@TestConfiguration
public class TestConfig {

    @Bean
    public ConversionService conversionService() {
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(new ReviewToReviewDTO());
        conversionService.addConverter(new ReviewDTOToReview());
        return conversionService;
    }
}
