package com.v2soft.productrating.services;

import org.springframework.stereotype.Service;

@Service
public interface AuthorizationService {
    boolean verifyToken(String token);
}
