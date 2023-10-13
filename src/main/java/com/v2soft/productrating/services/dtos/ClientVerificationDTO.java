package com.v2soft.productrating.services.dtos;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ClientVerificationDTO {

    private String clientId;
    private String clientSecret;
}
