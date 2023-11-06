package com.v2soft.productrating.services;

import com.v2soft.productrating.domain.ImageDetails;
import com.v2soft.productrating.services.dtos.MultipartFileDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

public interface ImageService {

    ImageDetails saveImage(MultipartFile imageFile);

    MultipartFileDetails fetchImage(String imageId) throws FileNotFoundException;

    MultipartFile fetchImageForAttachment(String imageId) throws FileNotFoundException;

    void deleteImage(String imageId);
}
