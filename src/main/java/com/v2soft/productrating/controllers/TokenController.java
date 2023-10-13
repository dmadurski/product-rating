package com.v2soft.productrating.controllers;

import com.v2soft.productrating.services.TokenService;
import com.v2soft.productrating.services.dtos.ClientVerificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/jwt")
public class TokenController {

    final TokenService tokenService;

    @GetMapping("/generate/{tokenFunction}")
    public ResponseEntity<Object> generateJwt(@PathVariable String tokenFunction, @RequestBody String clientId){

        return tokenService.generateJWT(tokenFunction, clientId);
    }

    @PreAuthorize("@authorizationServiceImpl.verifyClient(#clientVerificationDTO.clientId, #clientVerificationDTO.clientSecret)")
    @GetMapping("/retrieve/{tokenFunction}")
    public ResponseEntity<Object> retrieveJwt(@PathVariable String tokenFunction, @RequestBody ClientVerificationDTO clientVerificationDTO){
        return tokenService.retrieveJWT(tokenFunction, clientVerificationDTO.getClientId());
    }
}
