package com.v2soft.productrating.services;

import com.v2soft.productrating.domain.Review;
import com.v2soft.productrating.repositories.UserRepository;
import com.v2soft.productrating.repositories.ReviewRepository;
import com.v2soft.productrating.services.converters.ReviewDTOToReview;
import com.v2soft.productrating.services.converters.ReviewToReviewDTO;
import com.v2soft.productrating.services.dtos.ReviewDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private MongoTemplate mongoTemplate;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReviewToReviewDTO reviewToReviewDTOConverter;
    @Mock
    private ReviewDTOToReview reviewDTOToReviewConverter;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    List<Review> reviewList;

    @BeforeEach
    void setUp() {
        reviewList = new ArrayList<>();
        reviewList.add(new Review("1", "11111", "First", "Person", 12345, "Product 1", 5, "Great Product", LocalDateTime.now(), new ArrayList<>(), 1));
        reviewList.add(new Review("2", "11112","Second", "Person", 12345, "Product 1", 5, "Great Product", LocalDateTime.now(), new ArrayList<>(), 1));
        reviewList.add(new Review("3", "11113","Third", "Person", 12345, "Product 1", 5, "Great Product", LocalDateTime.now(), new ArrayList<>(), 1));
        reviewList.add(new Review("4", "11114","Fourth", "Person", 12345, "Product 2", 5, "Great Product", LocalDateTime.now(), new ArrayList<>(), 1));
        reviewList.add(new Review("5", "11115","Fifth", "Person", 12345, "Product 2", 5, "Great Product", LocalDateTime.now(), new ArrayList<>(), 1));
        reviewList.add(new Review("6", "11116","Sixth", "Person", 12345, "Product 2", 5, "Great Product", LocalDateTime.now(), new ArrayList<>(), 1));
        reviewList.add(new Review("7", "11117","Seventh", "Person", 12345, "Product 3", 5, "Great Product", LocalDateTime.now(), new ArrayList<>(), 1));
        reviewList.add(new Review("8", "11118","Eighth", "Person", 12345, "Product 3", 5, "Great Product", LocalDateTime.now(), new ArrayList<>(), 1));
        reviewList.add(new Review("9", "11119","Ninth", "Person", 12345, "Product 3", 5, "Great Product", LocalDateTime.now(), new ArrayList<>(), 1));
        reviewList.add(new Review("10", "11120","Tenth", "Person", 12345, "Product 3", 5, "Great Product", LocalDateTime.now(), new ArrayList<>(), 1));
        reviewList.add(new Review("11", "11121","Eleventh", "Person", 12345, "Product 4", 5, "Great Product", LocalDateTime.now(), new ArrayList<>(), 1));

        //mock the converter functionality
        when(reviewToReviewDTOConverter.convert(any(Review.class))).thenAnswer(invocation -> {
            Review review = invocation.getArgument(0);
            return convertReviewToReviewDTO(review); // Implement a method to create ReviewDTO based on Review
        });

        // Use reflection to set the values of the @Value annotated fields
        setField(reviewService, "maximumVersionHistory", 4);
        setField(reviewService, "poorScore", 4);
        setField(reviewService, "sortLimit", 10);
    }

    private void setField(Object target, String fieldName, Object value) {
        Field field = ReflectionUtils.findField(target.getClass(), fieldName);
        field.setAccessible(true);
        ReflectionUtils.setField(field, target, value);
    }

    public ReviewDTO convertReviewToReviewDTO(Review source) {
        ReviewDTO reviewDTO = new ReviewDTO();

        reviewDTO.setRatingId(source.getRatingId());
        reviewDTO.setFirstName(source.getFirstName());
        reviewDTO.setLastName(source.getLastName());
        reviewDTO.setProduct(source.getProduct());
        reviewDTO.setZipcode(source.getZipcode());
        reviewDTO.setScore(source.getScore());
        reviewDTO.setComment(source.getComment());
        reviewDTO.setDateAndTime(source.getDateAndTime());

        return reviewDTO;
    }

    @Test
    void findAll() {
        //when
        when(reviewRepository.findAll()).thenReturn(reviewList);
        List<ReviewDTO> foundReviews = reviewService.findAll();

        //then
        assertNotNull(foundReviews);
        assertEquals(11, foundReviews.size());
        assertNotNull(foundReviews.get(1));
        verify(reviewRepository, times(1)).findAll();
        verify(reviewToReviewDTOConverter, times(11)).convert(any(Review.class));
    }

    @Test
    void findLastTenReviews() {
        //when
        when(reviewRepository.findTop10ByOrderByDateAndTimeDesc()).thenReturn(reviewList.stream().limit(10).toList());
        List<ReviewDTO> foundReviews = reviewService.findLastTenReviews();

        //then
        assertNotNull(foundReviews);
        assertEquals(10, foundReviews.size());
        assertNotNull(foundReviews.get(1));
        verify(reviewRepository, times(1)).findTop10ByOrderByDateAndTimeDesc();
        verify(reviewToReviewDTOConverter, times(10)).convert(any(Review.class));
    }

//    @Test
//    void findReviewByReviewCode() {
//    }
//
//    @Test
//    void findReviewsByProductName() {
//    }
//
//    @Test
//    void saveReview() {
//    }
//
//    @Test
//    void deleteAllReviews() {
//    }
//
//    @Test
//    void deleteSpecificReview() {
//    }
//
//    @Test
//    void updateReview() {
//    }
}