package com.v2soft.productrating.controllers;

import com.v2soft.productrating.services.TokenService;
import com.v2soft.productrating.services.dtos.UserVerificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/jwt")
public class TokenController {

    final TokenService tokenService;

    @GetMapping("/regenJwt")
    public ResponseEntity<Object> regenerateJwt(@RequestParam String userId){
        return tokenService.regenerateJwt(userId);
    }

    @GetMapping("/generate/{tokenFunction}")
    public ResponseEntity<Object> generateJwt(@PathVariable String tokenFunction, @RequestBody String userId){

        return tokenService.generateFunctionJWT(tokenFunction, userId);
    }

    @PreAuthorize("@authorizationServiceImpl.verifyUser(#userVerificationDTO.userId, #userVerificationDTO.userSecret)")
    @GetMapping("/retrieve/{tokenFunction}")
    public ResponseEntity<Object> retrieveJwt(@PathVariable String tokenFunction, @RequestBody UserVerificationDTO userVerificationDTO){
        return tokenService.retrieveJWT(tokenFunction, userVerificationDTO.getUserId());
    }
}
