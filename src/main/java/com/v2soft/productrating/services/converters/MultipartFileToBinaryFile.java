package com.v2soft.productrating.services.converters;

import com.v2soft.productrating.domain.BinaryFile;
import org.bson.types.Binary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class MultipartFileToBinaryFile implements Converter<MultipartFile, BinaryFile> {

    @Override
    public BinaryFile convert(MultipartFile source) {
        BinaryFile imageFile = new BinaryFile();
        imageFile.setImageId(UUID.randomUUID().toString());
        imageFile.setFileName(source.getOriginalFilename());
        try {
            imageFile.setBinaryData(new Binary(source.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        imageFile.setFileType(source.getContentType());

        return imageFile;
    }
}
