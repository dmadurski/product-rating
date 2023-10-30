package com.v2soft.productrating.services;

import com.google.common.hash.Hashing;
import com.v2soft.productrating.domain.User;
import com.v2soft.productrating.repositories.UserRepository;
import com.v2soft.productrating.services.dtos.LoginRequestDTO;
import com.v2soft.productrating.services.dtos.UserDTO;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private static final Logger infoAndDebuglogger = LogManager.getLogger("InfoAndDebugLogger");

    final UserRepository userRepository;
    @Override
    public ResponseEntity<Object> createNewUser(UserDTO userDTO) throws IllegalArgumentException {
        String userFirstName = userDTO.getFirstName();
        String userLastName = userDTO.getLastName();
        String userUserName = userDTO.getUserName();
        String userPassword = userDTO.getPassword();
        String userRole = userDTO.getRole();

        if (userPassword == null || userPassword.isEmpty()) {
            throw new IllegalArgumentException("User secret must be a non-empty string");
        }

        if (userRepository.findByUserName(userUserName).isPresent()){
            return new ResponseEntity<>("That username is already in use, please choose another one.", HttpStatus.CONFLICT);
        }

        //If an admin account is created on the backend, run the password through a SHA256 hash to match how angular will send the password during login
        if (userRole.equals("ADMIN")){
            userPassword = Hashing.sha256()
                    .hashString(userPassword, StandardCharsets.UTF_8)
                    .toString();
        }

        //Generate a random salt, ID, hash the secret, then save them to a new user
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        String tokenSalt = Base64.getEncoder().encodeToString(saltBytes);

        String userID = java.util.UUID.randomUUID().toString();

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String hashedPassword = encoder.encode(userPassword);

        User newUser = new User(userID, userFirstName, userLastName, userUserName, hashedPassword, tokenSalt, userRole);
        userRepository.save(newUser);

        //Create a new LoginRequestDTO and return it in order to log in with the created credentials
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(userUserName, userPassword);
        return ResponseEntity.ok(loginRequestDTO);
    }

    @Override
    public Optional<User> findUserById(String id) {

        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findUserByUserName(String id) {

        return userRepository.findByUserName(id);
    }
}
