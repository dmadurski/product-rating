package com.v2soft.productrating.controllers;

import com.v2soft.productrating.domain.ImageDetails;
import com.v2soft.productrating.services.AuthorizationService;
import com.v2soft.productrating.services.ImageService;
import com.v2soft.productrating.services.dtos.MultipartFileDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class ImageController {

    final ImageService imageService;
    final AuthorizationService authorizationService;

    @PreAuthorize("@authorizationServiceImpl.verifyGenericToken(#token)")
    @PostMapping("/saveImage")
    public ImageDetails saveImage(@RequestHeader(name="Authorization") String token, @RequestParam MultipartFile imageFile) {
        return imageService.saveImage(imageFile);
    }

    @GetMapping("/fetchImage")
    public ResponseEntity<Object> fetchImage(@RequestParam String imageId) {
        try {
            MultipartFileDetails imageInfo = imageService.fetchImage(imageId);
            return ResponseEntity.ok(imageInfo);
        } catch (FileNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
