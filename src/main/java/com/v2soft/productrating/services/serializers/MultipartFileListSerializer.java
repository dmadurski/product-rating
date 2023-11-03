package com.v2soft.productrating.services.serializers;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.v2soft.productrating.services.dtos.MultipartFileDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MultipartFileListSerializer extends JsonSerializer<List<MultipartFile>> {

    @Override
    public void serialize(List<MultipartFile> fileList, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        List<MultipartFileDetails> multipartFileDetailsList = new ArrayList<>();

        for (MultipartFile file : fileList) {
            try {
                MultipartFileDetails multipartFileDetails = new MultipartFileDetails();
                multipartFileDetails.setOriginalFilename(file.getOriginalFilename());
                multipartFileDetails.setContent(file.getBytes());
                multipartFileDetails.setContentType(file.getContentType());

                multipartFileDetailsList.add(multipartFileDetails);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // Serialize the custom list as a JSON array
        serializers.defaultSerializeValue(multipartFileDetailsList, gen);
    }
}
