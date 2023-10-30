package com.v2soft.productrating.services.dtos;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    String userId;
    String firstName;
    String lastName;
    String token;
    String role;
}
