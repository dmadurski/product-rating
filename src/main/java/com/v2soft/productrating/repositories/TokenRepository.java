package com.v2soft.productrating.repositories;

import com.v2soft.productrating.domain.Token;

public interface TokenRepository {
    void save(Token token, String collectionName);

    Token findByCollectionAndOwnerId(String collectionName, String ownerId);
}
