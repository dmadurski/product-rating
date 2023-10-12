package com.v2soft.productrating.services.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    @JsonIgnore
    public String ratingCode;
    public String firstName;
    public String lastName;
    public int zipcode;
    public String product;
    public int score;
    public String comment;
}
