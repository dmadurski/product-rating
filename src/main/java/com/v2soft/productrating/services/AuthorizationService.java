package com.v2soft.productrating.services;

public interface AuthorizationService {
    boolean verifyToken(String token, String tokenFunction, String clientId);

    boolean verifyClient(String clientId, String clientSecret);
}
