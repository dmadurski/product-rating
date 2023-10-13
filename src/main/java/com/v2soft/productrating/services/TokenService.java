package com.v2soft.productrating.services;

import org.springframework.http.ResponseEntity;

public interface TokenService {

    ResponseEntity<Object> generateJWT(String tokenFunction, String clientId);

    ResponseEntity<Object> retrieveJWT(String tokenFunction, String clientId);
}
