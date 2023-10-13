package com.v2soft.productrating.services;

import com.v2soft.productrating.domain.Client;
import com.v2soft.productrating.repositories.ClientRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ClientServiceImpl implements ClientService{

    private static final Logger infoAndDebuglogger = LogManager.getLogger("InfoAndDebugLogger");

    final ClientRepository clientRepository;
    @Override
    public String createNewClient(String clientSecret) {
        //Generate a random salt, ID, hash the secret, then save them to a new client
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        String tokenSalt = Base64.getEncoder().encodeToString(saltBytes);

        String clientID = java.util.UUID.randomUUID().toString();

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String hashedSecret = encoder.encode(clientSecret);

        Client newClient = new Client(clientID, tokenSalt, hashedSecret);
        clientRepository.save(newClient);

        return "Client successfully created";
    }

    @Override
    public Optional<Client> findClientById(String id) {

        return clientRepository.findById(id);
    }
}
