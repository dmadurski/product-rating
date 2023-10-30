package com.v2soft.productrating.services;

import com.v2soft.productrating.domain.User;
import io.jsonwebtoken.LocatorAdapter;
import io.jsonwebtoken.ProtectedHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Optional;

@Service
public class KeyLocator extends LocatorAdapter<Key> {

    @Value("${jwt.generic.pepper}")
    private String genericPepper;

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Key locate(ProtectedHeader header) { // a JwsHeader or JweHeader
        // keyId matches the owners userId, so use keyId to find the user and retrieve their salt
        String keyId = header.getKeyId();
        Optional<User> keyOwnerOptional = userService.findUserById(keyId);
        if (keyOwnerOptional.isPresent()){
            User keyOwner = keyOwnerOptional.get();
            String userSalt = keyOwner.getTokenSalt();

            // Decode tokenSalt and tokenPepper
            byte[] saltBytesDecoded = Base64.getDecoder().decode(userSalt);
            byte[] pepperBytesDecoded = Base64.getDecoder().decode(genericPepper);

            // Concatenate the two byte arrays to create the secret key bytes
            byte[] secretKeyBytes = new byte[saltBytesDecoded.length + pepperBytesDecoded.length];
            System.arraycopy(saltBytesDecoded, 0, secretKeyBytes, 0, saltBytesDecoded.length);
            System.arraycopy(pepperBytesDecoded, 0, secretKeyBytes, saltBytesDecoded.length, pepperBytesDecoded.length);

            // Create the SecretKey using the combined bytes
            return new SecretKeySpec(secretKeyBytes, "HmacSHA256");
        } else {
            // Did not find a user matching the keyId (should never happen)
            return null;
        }
    }
}