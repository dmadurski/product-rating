package com.v2soft.productrating.services;

import com.v2soft.productrating.domain.Client;
import com.v2soft.productrating.repositories.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    void createNewClient() {
        //given
        String clientSecret = "exampleSecret";

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String hashedSecret = encoder.encode(clientSecret);

        //when
        when(clientRepository.save(Mockito.isA(Client.class))).thenAnswer(input -> input.getArgument(0));

        Client createdClient = clientService.createNewClient(clientSecret);

        //then
        verify(clientRepository, times(1)).save(any(Client.class));
        assertNotNull(createdClient);
        assertTrue(encoder.matches(clientSecret, hashedSecret));
    }

    @Test
    void findClientById() {
        //given
        String clientId = "12345";

        //when
        when(clientRepository.findById(Mockito.anyString())).thenReturn(Optional.of(new Client(clientId, "salt", "hashedSecret")));

        Optional<Client> foundClientOptional = clientService.findClientById(clientId);

        //then
        verify(clientRepository, times(1)).findById(anyString());
        assertTrue(foundClientOptional.isPresent());
        assertEquals(clientId, foundClientOptional.get().getClientId());
    }
}