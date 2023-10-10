package com.v2soft.productrating.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document("reviews")
public class Review {
    @Id
    String ratingCode;

    public String firstName;
    public String lastName;
    public int zipcode;
    public String product;
    public int score;
    public String comment;
    public LocalDateTime dateAndTime;
    public List<Review> previousVersions;
    public int versionCount;

    public String reviewCollectionToString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDateTime = this.dateAndTime.format(formatter);
        return (this.product + " | " + this.firstName + " | "
                + this.lastName + " | " + this.zipcode + " | " + formattedDateTime);
    }
}
