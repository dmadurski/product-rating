package com.v2soft.productrating.services;

import org.springframework.http.ResponseEntity;

public interface TokenService {

    ResponseEntity<Object> generateJWT(String userName);

    ResponseEntity<Object> regenerateJwt(String userId);

    ResponseEntity<Object> generateFunctionJWT(String tokenFunction, String userId);

    ResponseEntity<Object> retrieveJWT(String tokenFunction, String userId);
}
