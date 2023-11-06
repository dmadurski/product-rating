package com.v2soft.productrating.services;


import com.v2soft.productrating.domain.BinaryFile;
import com.v2soft.productrating.domain.ImageDetails;
import com.v2soft.productrating.repositories.ImageRepository;
import com.v2soft.productrating.services.converters.BinaryFileToMultipartFile;
import com.v2soft.productrating.services.converters.BinaryFileToMultipartFileDetails;
import com.v2soft.productrating.services.converters.MultipartFileToBinaryFile;
import com.v2soft.productrating.services.dtos.MultipartFileDetails;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {

    final ImageRepository imageRepository;
    final MultipartFileToBinaryFile multipartFileToBinaryFileConverter;
    final BinaryFileToMultipartFileDetails binaryFileToMultipartFileDetailsConverter;
    final BinaryFileToMultipartFile binaryFileToMultipartFileConverter;

    private static final Logger infoAndDebuglogger = LogManager.getLogger("InfoAndDebugLogger");

    @Override
    public ImageDetails saveImage(MultipartFile multipartFile) {
        BinaryFile imageFile = multipartFileToBinaryFileConverter.convert(multipartFile);
        imageRepository.save(imageFile);
        infoAndDebuglogger.info("Successfully saved review image");
        return new ImageDetails(imageFile.getImageId(), imageFile.getFileName());
    }

    @Override
    public MultipartFileDetails fetchImage(String imageId) throws FileNotFoundException {
        Optional<BinaryFile> binaryFileOptional = imageRepository.findById(imageId);
        if (binaryFileOptional.isPresent()){
            BinaryFile foundBinaryFile = binaryFileOptional.get();
            return binaryFileToMultipartFileDetailsConverter.convert(foundBinaryFile);
        } else {
            infoAndDebuglogger.debug("Unable to find image file with id: " + imageId);
            throw new FileNotFoundException("Unable to find image file with id: " + imageId);
        }
    }

    @Override
    public MultipartFile fetchImageForAttachment(String imageId) throws FileNotFoundException {
        Optional<BinaryFile> binaryFileOptional = imageRepository.findById(imageId);
        if (binaryFileOptional.isPresent()){
            BinaryFile foundBinaryFile = binaryFileOptional.get();
            return binaryFileToMultipartFileConverter.convert(foundBinaryFile);
        } else {
            infoAndDebuglogger.debug("Unable to find image file with id: " + imageId);
            throw new FileNotFoundException("Unable to find image file with id: " + imageId);
        }
    }

    @Override
    public void deleteImage(String imageId) {
        imageRepository.deleteById(imageId);
    }
}
