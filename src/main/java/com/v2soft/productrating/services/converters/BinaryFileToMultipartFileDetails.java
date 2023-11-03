package com.v2soft.productrating.services.converters;

import com.v2soft.productrating.domain.BinaryFile;
import com.v2soft.productrating.services.dtos.MultipartFileDetails;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class BinaryFileToMultipartFileDetails implements Converter<BinaryFile, MultipartFileDetails> {

    @Override
    public MultipartFileDetails convert(BinaryFile source) {
        String originalFileName = source.getFileName();
        byte[] fileContent = source.getBinaryData().getData();
        String originalFileType = source.getFileType();

        return new MultipartFileDetails(originalFileName, fileContent, originalFileType);
    }
}
