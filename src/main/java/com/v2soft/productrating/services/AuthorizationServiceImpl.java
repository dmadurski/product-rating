package com.v2soft.productrating.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    @Value("${jwt.secret.key}")
    private String secret;

    private static final Logger infoAndDebuglogger = LogManager.getLogger("InfoAndDebugLogger");


    @Override
    public boolean verifyToken(String token) {
        Jws<Claims> jws;

        byte[] secretKeyBytes = Base64.getDecoder().decode(secret);
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, "HmacSHA256");

        try {

            jws = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            infoAndDebuglogger.info("Jwt verified");
            return true;
        } catch (JwtException ex) {
            infoAndDebuglogger.debug(ex);
            return false;
        }
    }
}
