package com.v2soft.productrating.repositories;

import com.v2soft.productrating.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUserNameIgnoreCase(String userName);
}
