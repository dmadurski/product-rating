package com.v2soft.productrating.services;

import org.springframework.http.ResponseEntity;

public interface AuthorizationService {
    boolean verifyToken(String token, String tokenFunction, String userId);

    boolean verifyGenericToken(String token);

    ResponseEntity<Object> verifyGenericTokenWithResponse(String token);

    boolean verifyUser(String userId, String userSecret);

    boolean verifyLogin(String userName, String password);
}
