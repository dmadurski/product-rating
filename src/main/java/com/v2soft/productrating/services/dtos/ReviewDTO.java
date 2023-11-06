package com.v2soft.productrating.services.dtos;

import com.v2soft.productrating.domain.ImageDetails;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    public String ratingId;
    public String userId;
    public String firstName;
    public String lastName;
    public int zipcode;
    public String email;
    public String product;
    public int score;
    public String comment;
    public List<ImageDetails> imageDetailsList;
    public LocalDateTime dateAndTime;
}
