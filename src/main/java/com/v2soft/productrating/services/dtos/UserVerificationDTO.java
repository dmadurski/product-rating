package com.v2soft.productrating.services.dtos;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserVerificationDTO {

    private String userId;
    private String userSecret;
}
