package com.v2soft.productrating.services;

import com.v2soft.productrating.domain.Client;

import java.util.Optional;

public interface ClientService {

    String createNewClient(String clientSecret);

    Optional<Client> findClientById(String id);
}
