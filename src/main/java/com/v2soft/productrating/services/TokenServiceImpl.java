package com.v2soft.productrating.services;

import com.v2soft.productrating.domain.User;
import com.v2soft.productrating.domain.Token;
import com.v2soft.productrating.repositories.TokenRepository;
import com.v2soft.productrating.services.dtos.LoginResponseDTO;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class TokenServiceImpl implements TokenService {

    private static final Logger infoAndDebuglogger = LogManager.getLogger("InfoAndDebugLogger");

    final TokenRepository tokenRepository;
    final UserService userService;

    @Value("${jwt.update.pepper}")
    private String updatePepper;

    @Value("${jwt.delete.pepper}")
    private String deletePepper;

    @Value("${jwt.generic.pepper}")
    private String genericPepper;

    @Value("${delete.token.collection}")
    private String deleteCollection;

    @Value("${update.token.collection}")
    private String updateCollection;

    final KeyLocator keyLocator;

    //Method for Frontend access
    @Override
    public ResponseEntity<Object> generateJWT(String userName){
        Optional<User> userOptional = userService.findUserByUserName(userName);
        if (userOptional.isEmpty()) {
            infoAndDebuglogger.debug("Unable to find user with userName: " + userName);
            return new ResponseEntity<>("No user found with that userName", HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();
        String tokenSalt = user.getTokenSalt();
        Date expiration = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2));

        // Decode tokenSalt and tokenPepper
        byte[] saltBytesDecoded = Base64.getDecoder().decode(tokenSalt);
        byte[] pepperBytesDecoded = Base64.getDecoder().decode(genericPepper);

        // Concatenate the two byte arrays to create the secret key bytes
        byte[] secretKeyBytes = new byte[saltBytesDecoded.length + pepperBytesDecoded.length];
        System.arraycopy(saltBytesDecoded, 0, secretKeyBytes, 0, saltBytesDecoded.length);
        System.arraycopy(pepperBytesDecoded, 0, secretKeyBytes, saltBytesDecoded.length, pepperBytesDecoded.length);

        // Create the SecretKey using the combined bytes
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, "HmacSHA256");

        String jwtID = java.util.UUID.randomUUID().toString();
        String jwtString = Jwts.builder()
                .header().keyId(user.getUserId()).and()
                .expiration(expiration)
                .id(jwtID)
                .signWith(secretKey)
                .compact();

        LoginResponseDTO loginResponse = new LoginResponseDTO(user.getUserId(), user.getFirstName(), user.getLastName(), jwtString, user.getRole());

        return ResponseEntity.ok(loginResponse);
    }

    @Override
    public ResponseEntity<Object> regenerateJwt(String userId){
        Optional<User> userOptional = userService.findUserById(userId);
        if (userOptional.isEmpty()) {
            infoAndDebuglogger.debug("Unable to find user with userId: " + userId);
            return new ResponseEntity<>("No user found with that userId", HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();
        String tokenSalt = user.getTokenSalt();
        Date expiration = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2));

        // Decode tokenSalt and tokenPepper
        byte[] saltBytesDecoded = Base64.getDecoder().decode(tokenSalt);
        byte[] pepperBytesDecoded = Base64.getDecoder().decode(genericPepper);

        // Concatenate the two byte arrays to create the secret key bytes
        byte[] secretKeyBytes = new byte[saltBytesDecoded.length + pepperBytesDecoded.length];
        System.arraycopy(saltBytesDecoded, 0, secretKeyBytes, 0, saltBytesDecoded.length);
        System.arraycopy(pepperBytesDecoded, 0, secretKeyBytes, saltBytesDecoded.length, pepperBytesDecoded.length);

        // Create the SecretKey using the combined bytes
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, "HmacSHA256");

        String jwtID = java.util.UUID.randomUUID().toString();
        String jwtString = Jwts.builder()
                .header().keyId(user.getUserId()).and()
                .expiration(expiration)
                .id(jwtID)
                .signWith(secretKey)
                .compact();

        return ResponseEntity.ok(jwtString);
    }

    //Method for API access
    @Override
    public ResponseEntity<Object> generateFunctionJWT(String tokenFunction, String userId){
        Optional<User> userOptional = userService.findUserById(userId);
        if (userOptional.isEmpty()) {
            infoAndDebuglogger.debug("Unable to find user with id: " + userId);
            return new ResponseEntity<>("No user found with that id", HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();

        String tokenSalt = user.getTokenSalt();
        String tokenPepper;
        String collectionName;

        //Determine which pepper & collectionName to use
        switch (tokenFunction.toLowerCase()) {
            case "delete":
                tokenPepper = deletePepper;
                collectionName = deleteCollection;
                break;
            case "update":
                tokenPepper = updatePepper;
                collectionName = updateCollection;
                break;
            default:
                return new ResponseEntity<>("Not a valid token request", HttpStatus.BAD_REQUEST);
        }

        Date expiration = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));

        // Decode tokenSalt and tokenPepper
        byte[] saltBytesDecoded = Base64.getDecoder().decode(tokenSalt);
        byte[] pepperBytesDecoded = Base64.getDecoder().decode(tokenPepper);

        // Concatenate the two byte arrays to create the secret key bytes
        byte[] secretKeyBytes = new byte[saltBytesDecoded.length + pepperBytesDecoded.length];
        System.arraycopy(saltBytesDecoded, 0, secretKeyBytes, 0, saltBytesDecoded.length);
        System.arraycopy(pepperBytesDecoded, 0, secretKeyBytes, saltBytesDecoded.length, pepperBytesDecoded.length);

        // Create the SecretKey using the combined bytes
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, "HmacSHA256");

        String jwtID = java.util.UUID.randomUUID().toString();
        String jwtString = Jwts.builder()
                .expiration(expiration)
                .id(jwtID)
                .signWith(secretKey)
                .compact();

        Token newJwt = new Token(jwtID, userId, jwtString, expiration);
        tokenRepository.save(newJwt, collectionName);

        return ResponseEntity.ok(newJwt);
    }

    @Override
    public ResponseEntity<Object> retrieveJWT(String tokenFunction, String userId) {
        Optional<User> userOptional = userService.findUserById(userId);
        if (userOptional.isEmpty()) {
            infoAndDebuglogger.debug("Unable to find user with id: " + userId);
            return new ResponseEntity<>("No user found with that id", HttpStatus.NOT_FOUND);
        }

        //Determine which collectionName to use for searching
        String collectionName;
        switch (tokenFunction.toLowerCase()) {
            case "delete":
                collectionName = deleteCollection;
                break;
            case "update":
                collectionName = updateCollection;
                break;
            default:
                return new ResponseEntity<>(tokenFunction + " is not a proper function. Please try again", HttpStatus.NOT_FOUND);
        }

        Token foundToken = tokenRepository.findByCollectionAndOwnerId(collectionName, userId);

        if(foundToken != null) {
            return ResponseEntity.ok(foundToken);
        } else {
            infoAndDebuglogger.debug("No unexpired token found, attempting to generate a new one");
            return generateFunctionJWT(tokenFunction, userId);
        }
    }
}