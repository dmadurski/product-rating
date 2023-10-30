package com.v2soft.productrating.services;

import com.v2soft.productrating.domain.User;
import com.v2soft.productrating.services.dtos.UserDTO;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserService {

    ResponseEntity<Object> createNewUser(UserDTO userDTO);

    Optional<User> findUserById(String id);

    Optional<User> findUserByUserName(String id);
}
