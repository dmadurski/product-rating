package com.v2soft.productrating.repositories;

import com.v2soft.productrating.domain.Client;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClientRepository extends MongoRepository<Client, String> {
}
