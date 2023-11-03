package com.v2soft.productrating.repositories;

import com.v2soft.productrating.domain.BinaryFile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageRepository extends MongoRepository<BinaryFile, String> {
}
