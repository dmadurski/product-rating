package com.v2soft.productrating.services;

import com.v2soft.productrating.domain.User;
import com.v2soft.productrating.domain.Token;
import com.v2soft.productrating.repositories.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;

import java.util.Optional;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TokenServiceImpl tokenService;

    String tokenFunction;
    String clientId;
    String userFirstName;
    String userLastName;
    String userUserName;
    String userPassword;
    User user;

    @BeforeEach
    void setUp() {
        tokenFunction = "update";
        clientId = "12345";
        userFirstName = "firstName";
        userLastName = "lastName";
        userUserName = "userName";
        userPassword = "exampleSecret";
        user = new User(clientId, userFirstName, userLastName, userUserName, userPassword, "salt", "USER");

        // Use reflection to set the values of the @Value annotated fields
        setField(tokenService, "updatePepper", "aR+HXb4gLz7XHu5XJNNu4BzYrJVQejzaJPOEB/uWaig=");
        setField(tokenService, "deletePepper", "WiZY+hzJXK5NUpwHgqUYhJHlgdIwXehVRPQEO27X+yo=");
        setField(tokenService, "deleteCollection", "deletetokens");
        setField(tokenService, "updateCollection", "updatetokens");
    }

    private void setField(Object target, String fieldName, Object value) {
        Field field = ReflectionUtils.findField(target.getClass(), fieldName);
        field.setAccessible(true);
        ReflectionUtils.setField(field, target, value);
    }

    @Test
    void generateJWT() {
        //when
        when(userService.findUserById(clientId)).thenReturn(Optional.of(user));
        when(tokenRepository.save(Mockito.isA(Token.class), anyString())).thenAnswer(input -> input.getArgument(0));

        ResponseEntity<Object> response = tokenService.generateFunctionJWT(tokenFunction, clientId);
        Token createdToken = (Token) response.getBody();

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(Token.class, response.getBody());
        assertNotNull(createdToken);
        assertEquals(clientId, createdToken.getOwnerId());
        verify(tokenRepository, times(1)).save(any(Token.class), eq("updatetokens"));
    }

    @Test
    void generateJWTWithMissingClient() {
        //when
        when(userService.findUserById(clientId)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = tokenService.generateFunctionJWT(tokenFunction, clientId);

        //then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof String);
    }

    @Test
    void generateJWTWithInvalidFunction() {
        //given
        String badTokenFunction = "fakeFunction";

        //when
        when(userService.findUserById(clientId)).thenReturn(Optional.of(user));

        ResponseEntity<Object> response = tokenService.generateFunctionJWT(badTokenFunction, clientId);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof String);
    }

    @Test
    void retrieveJWT() {
        //when
        when(userService.findUserById(clientId)).thenReturn(Optional.of(user));

        ResponseEntity<Object> response = tokenService.retrieveJWT(tokenFunction, clientId);
        Token retrievedToken = (Token) response.getBody();

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(Token.class, response.getBody());
        assertNotNull(retrievedToken);
        assertEquals(clientId, retrievedToken.getOwnerId());
    }
}