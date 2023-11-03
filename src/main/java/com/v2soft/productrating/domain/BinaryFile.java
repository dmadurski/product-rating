package com.v2soft.productrating.domain;

import lombok.*;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document("images")
public class BinaryFile {
    @Id
    String imageId;
    public String fileName;
    public Binary binaryData;
    public String fileType;
}
