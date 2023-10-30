package com.v2soft.productrating.services;

public interface AuthorizationService {
    boolean verifyToken(String token, String tokenFunction, String userId);

    boolean verifyGenericToken(String token);

    boolean verifyUser(String userId, String userSecret);

    boolean verifyLogin(String userName, String password);
}
