package com.v2soft.productrating.services.dtos;

import lombok.*;

import java.time.LocalDateTime;

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
    public String product;
    public int score;
    public String comment;
    public LocalDateTime dateAndTime;
}
