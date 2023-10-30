package com.v2soft.productrating.services.dtos;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    String userName;
    String password;
}
