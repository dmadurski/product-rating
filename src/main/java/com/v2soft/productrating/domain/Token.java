package com.v2soft.productrating.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Token {

    @Id
    String id;
    public String ownerId; //Should match a valid userId
    public String jwtString;
    public Date expirationDate;
}
