package com.v2soft.productrating.repositories;

import com.v2soft.productrating.domain.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TokenRepository extends MongoRepository<Token, String> {
}
