package com.v2soft.productrating.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MultipartFileDetails {
    private String originalFilename;
    private byte[] content;
    private String contentType;
}
