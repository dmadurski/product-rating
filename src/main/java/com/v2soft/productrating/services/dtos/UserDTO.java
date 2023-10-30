package com.v2soft.productrating.services.dtos;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    String firstName;
    String lastName;
    String userName;
    String password;
    String role;
}
