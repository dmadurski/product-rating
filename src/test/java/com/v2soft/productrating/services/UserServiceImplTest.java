package com.v2soft.productrating.services;

import com.v2soft.productrating.domain.User;
import com.v2soft.productrating.repositories.UserRepository;
import com.v2soft.productrating.services.dtos.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl clientService;

    String userFirstName;
    String userLastName;
    String userUserName;
    String userSecret;
    UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userFirstName = "firstName";
        userLastName = "lastName";
        userUserName = "userName";
        userSecret = "exampleSecret";
        userDTO = new UserDTO(userFirstName, userLastName, userUserName, userSecret, "USER");
    }

    @Test
    void createNewClient() {
        //given
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String hashedSecret = encoder.encode(userSecret);

        //when
        when(userRepository.save(Mockito.isA(User.class))).thenAnswer(input -> input.getArgument(0));

        ResponseEntity<Object> response = clientService.createNewUser(userDTO);
        User createdUser = (User) response.getBody();

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof User);
        assertNotNull(createdUser);
        verify(userRepository, times(1)).save(any(User.class));
        assertTrue(encoder.matches(userSecret, hashedSecret));
    }

    @Test
    void findUserById() {
        //given
        String clientId = "12345";
        String tokenSalt = "exampleSalt";

        //when
        when(userRepository.findById(Mockito.anyString())).thenReturn(Optional.of(new User(clientId, userFirstName, userLastName, userUserName, tokenSalt, userSecret, "USER")));

        Optional<User> foundClientOptional = clientService.findUserById(clientId);

        //then
        verify(userRepository, times(1)).findById(anyString());
        assertTrue(foundClientOptional.isPresent());
        assertEquals(clientId, foundClientOptional.get().getUserId());
    }
}