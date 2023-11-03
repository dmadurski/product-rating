package com.v2soft.productrating.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document("users")
public class User {
    @Id
    String userId;
    public String firstName;
    public String lastName;
    public String userName;
    public String hashedPassword;
    public String tokenSalt;
    public String role;
}