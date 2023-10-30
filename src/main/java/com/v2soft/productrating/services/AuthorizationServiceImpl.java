package com.v2soft.productrating.services;

import com.v2soft.productrating.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    @Value("${jwt.update.pepper}")
    private String updatePepper;

    @Value("${jwt.delete.pepper}")
    private String deletePepper;

    private static final Logger infoAndDebuglogger = LogManager.getLogger("InfoAndDebugLogger");

    final UserService userService;
    final KeyLocator keyLocator;

    @Override
    public boolean verifyToken(String token, String userId, String tokenFunction) {
        infoAndDebuglogger.info("Reached the verifyToken Method");

        //If no user was found with the matching id, fail the authorization and log
        Optional<User> userOptional = userService.findUserById(userId);
        if (userOptional.isEmpty()) {
            infoAndDebuglogger.debug("Unable to find user with id: " + userId);
            return false;
        }

        User user = userOptional.get();
        String tokenSalt = user.getTokenSalt();

        //If the tokenFunction string does not match a value, then it is not a proper request: fail authorization
        String tokenPepper;
        if ("delete".equalsIgnoreCase(tokenFunction)) {
            tokenPepper = deletePepper;
        } else if ("update".equalsIgnoreCase(tokenFunction)) {
            tokenPepper = updatePepper;
        } else {
            infoAndDebuglogger.debug("Invalid tokenFunction provided: " + tokenFunction);
            return false;
        }

        // Decode tokenSalt and tokenPepper
        byte[] saltBytesDecoded = Base64.getDecoder().decode(tokenSalt);
        byte[] pepperBytesDecoded = Base64.getDecoder().decode(tokenPepper);

        // Concatenate the two byte arrays to create the secret key bytes
        byte[] secretKeyBytes = new byte[saltBytesDecoded.length + pepperBytesDecoded.length];
        System.arraycopy(saltBytesDecoded, 0, secretKeyBytes, 0, saltBytesDecoded.length);
        System.arraycopy(pepperBytesDecoded, 0, secretKeyBytes, saltBytesDecoded.length, pepperBytesDecoded.length);
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, "HmacSHA256");

        Jws<Claims> jws;
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

    @Override
    public boolean verifyGenericToken(String token) {
        infoAndDebuglogger.info("Reached the verifyGenericToken Method");

        Jws<Claims> jws;
        try {
            jws = Jwts.parser()
                    .keyLocator(keyLocator)
                    .build()
                    .parseSignedClaims(token);

            infoAndDebuglogger.info("Jwt verified");
            return true;
        } catch (JwtException ex) {
            infoAndDebuglogger.debug(ex);
            return false;
        }
    }

    @Override
    public boolean verifyUser(String userId, String password) {
        infoAndDebuglogger.info("Reached the verifyUser Method");

        //If no user was found with the matching id, fail the authorization and log
        Optional<User> userOptional = userService.findUserById(userId);
        if (userOptional.isEmpty()) {
            infoAndDebuglogger.debug("Unable to find user with id: " + userId);
            return false;
        }

        User user = userOptional.get();

        //Retrieve the hashed secret and check if it matches the secret provided during request
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String hashedSecret = user.getHashedPassword();

        if(!encoder.matches(password, hashedSecret)) {
            infoAndDebuglogger.debug("Provided secret did not match hashed secret.");
            return false;
        } else {
            infoAndDebuglogger.info("User authenticated");
            return true;
        }
    }

    @Override
    public boolean verifyLogin(String userName, String password) {
        infoAndDebuglogger.info("Reached the verifyLogin Method");

        //If no user was found with the matching id, fail the authorization and log
        Optional<User> userOptional = userService.findUserByUserName(userName);
        if (userOptional.isEmpty()) {
            infoAndDebuglogger.debug("Unable to find user with username: " + userName);
            return false;
        }

        User user = userOptional.get();

        //Retrieve the hashed secret and check if it matches the secret provided during request
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String hashedPassword = user.getHashedPassword();

        if(!encoder.matches(password, hashedPassword)) {
            infoAndDebuglogger.debug("Provided password did not match hashed password.");
            return false;
        } else {
            infoAndDebuglogger.info("User authenticated");
            return true;
        }
    }
}
