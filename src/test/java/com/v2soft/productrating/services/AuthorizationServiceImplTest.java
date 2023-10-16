package com.v2soft.productrating.services;

import com.v2soft.productrating.domain.Client;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ReflectionUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceImplTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private AuthorizationServiceImpl authorizationService;

    String tokenFunction;
    String clientId;
    Client client;
    String jwtString;
    SecretKey secretKey;

    @BeforeEach
    void setUp() {
        tokenFunction = "delete";
        clientId = "12345";

        String clientSalt = "salt";
        String clientSecret = "exampleSecret";
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String hashedSecret = encoder.encode(clientSecret);
        client = new Client(clientId, clientSalt, hashedSecret);

        //Create the secretKey for signing JWTs
        byte[] saltBytesDecoded = Base64.getDecoder().decode(clientSalt);
        byte[] pepperBytesDecoded = Base64.getDecoder().decode("WiZY+hzJXK5NUpwHgqUYhJHlgdIwXehVRPQEO27X+yo=");
        byte[] secretKeyBytes = new byte[saltBytesDecoded.length + pepperBytesDecoded.length];
        System.arraycopy(saltBytesDecoded, 0, secretKeyBytes, 0, saltBytesDecoded.length);
        System.arraycopy(pepperBytesDecoded, 0, secretKeyBytes, saltBytesDecoded.length, pepperBytesDecoded.length);
        secretKey = new SecretKeySpec(secretKeyBytes, "HmacSHA256");

        // Use reflection to set the values of the @Value annotated fields
        setField(authorizationService, "updatePepper", "aR+HXb4gLz7XHu5XJNNu4BzYrJVQejzaJPOEB/uWaig=");
        setField(authorizationService, "deletePepper", "WiZY+hzJXK5NUpwHgqUYhJHlgdIwXehVRPQEO27X+yo=");
    }

    private void setField(Object target, String fieldName, Object value) {
        Field field = ReflectionUtils.findField(target.getClass(), fieldName);
        field.setAccessible(true);
        ReflectionUtils.setField(field, target, value);
    }

    @Test
    void verifyToken() {
        //given
        Date expiration = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
        String jwtID = java.util.UUID.randomUUID().toString();
        jwtString = Jwts.builder()
                .expiration(expiration)
                .id(jwtID)
                .signWith(secretKey)
                .compact();

        //when
        when(clientService.findClientById(clientId)).thenReturn(Optional.of(client));
        boolean authResult = authorizationService.verifyToken(jwtString, clientId, tokenFunction);

        //then
        assertTrue(authResult);
    }

    @Test
    void verifyExpiredToken() {
        //given
        Date expiration = new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5));
        String jwtID = java.util.UUID.randomUUID().toString();
        jwtString = Jwts.builder()
                .expiration(expiration)
                .id(jwtID)
                .signWith(secretKey)
                .compact();

        //when
        when(clientService.findClientById(clientId)).thenReturn(Optional.of(client));
        boolean authResult = authorizationService.verifyToken(jwtString, clientId, tokenFunction);

        //then
        assertFalse(authResult);
    }

    @Test
    void verifyTokenWithMissingClient() {
        //given
        Date expiration = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
        String jwtID = java.util.UUID.randomUUID().toString();
        jwtString = Jwts.builder()
                .expiration(expiration)
                .id(jwtID)
                .signWith(secretKey)
                .compact();

        //when
        when(clientService.findClientById(clientId)).thenReturn(Optional.empty());
        boolean authResult = authorizationService.verifyToken(jwtString, clientId, tokenFunction);

        //then
        assertFalse(authResult);
    }

    @Test
    void verifyClient() {
        //when
        when(clientService.findClientById(clientId)).thenReturn(Optional.of(client));
        boolean authResult = authorizationService.verifyClient(clientId, "exampleSecret");

        //then
        assertTrue(authResult);
    }

    @Test
    void verifyClientBadSecret() {
        //when
        when(clientService.findClientById(clientId)).thenReturn(Optional.of(client));
        boolean authResult = authorizationService.verifyClient(clientId, "wrongSecret");

        //then
        assertFalse(authResult);
    }

    @Test
    void verifyMissingClient() {
        //when
        when(clientService.findClientById(clientId)).thenReturn(Optional.empty());
        boolean authResult = authorizationService.verifyClient(clientId, "exampleSecret");

        //then
        assertFalse(authResult);
    }
}