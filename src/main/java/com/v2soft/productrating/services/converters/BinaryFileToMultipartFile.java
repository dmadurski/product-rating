package com.v2soft.productrating.services.converters;

import com.v2soft.productrating.domain.BinaryFile;
import com.v2soft.productrating.services.dtos.ConvertedMultipartFile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BinaryFileToMultipartFile implements Converter<BinaryFile, MultipartFile> {

    @Override
    public MultipartFile convert(BinaryFile source) {
        String originalFileName = source.getFileName();
        byte[] fileContent = source.getBinaryData().getData();
        String originalFileType = source.getFileType();

        return new ConvertedMultipartFile(originalFileName, fileContent, originalFileType);
    }
}
