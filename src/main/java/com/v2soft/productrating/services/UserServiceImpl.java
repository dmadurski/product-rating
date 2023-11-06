package com.v2soft.productrating.services;

import com.google.common.hash.Hashing;
import com.v2soft.productrating.domain.ImageDetails;
import com.v2soft.productrating.domain.Review;
import com.v2soft.productrating.domain.User;
import com.v2soft.productrating.repositories.UserRepository;
import com.v2soft.productrating.services.dtos.LoginRequestDTO;
import com.v2soft.productrating.services.dtos.UserDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private static final Logger infoAndDebuglogger = LogManager.getLogger("InfoAndDebugLogger");

    final UserRepository userRepository;
    final ImageService imageService;
    final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;
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

        if (userRepository.findByUserNameIgnoreCase(userUserName).isPresent()){
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
    public Optional<User> findUserByUserName(String userName) {

        return userRepository.findByUserNameIgnoreCase(userName);
    }

    @Override
    public void notifyReviewSuccess(Review review) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
        String formattedDateTime = review.getDateAndTime().format(formatter);

        String reviewOwnerId = review.getUserId();
        Optional<User> reviewOwnerOptional = userRepository.findById(reviewOwnerId);

        if(reviewOwnerOptional.isPresent()){
            User reviewOwner = reviewOwnerOptional.get();
            String reviewOwnerEmail = review.getEmail();
            String emailBody = "Hello " + reviewOwner.getUserName() + "," +
                    "<br>Your review for " + review.getProduct() + " was successfully posted at " + formattedDateTime + " with the following information:" +
                    "<br>Score: " + review.getScore();

            if(!review.getComment().isEmpty()){
                emailBody = emailBody + "<br>Comment: " + review.getComment();
            }

            String emailSubject = "Review successfully posted for " + review.getProduct();

            try {
                mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
                mimeMessageHelper.setFrom(sender);
                mimeMessageHelper.setTo(reviewOwnerEmail);
                mimeMessageHelper.setSubject(emailSubject);

                if(!review.getImageDetailsList().isEmpty()){
                    List<MultipartFile> reviewImages = new ArrayList<MultipartFile>();
                    List<ImageDetails> imageDetailsList = review.getImageDetailsList();
                    for(ImageDetails imageDetails: imageDetailsList){
                        String imageId = imageDetails.getImageId();
                        try{
                            reviewImages.add(imageService.fetchImageForAttachment(imageId));
                        } catch (FileNotFoundException e){
                            //Cause of error already logged in imageService. No need to handle exception
                        }
                    }
                    //Add the images as attachments to the email
                    if(!reviewImages.isEmpty()) {
                        emailBody = emailBody + "<br><br>The files you sent with the review have been attached to this email for your record.";
                        for(MultipartFile image: reviewImages) {
                            //If the files list is not empty, then getOriginalFilename will never be null
                            mimeMessageHelper.addAttachment(image.getOriginalFilename(), image);
                        }
                    } else {
                        emailBody = emailBody + "<br><br>There was a problem attaching the images you included in your review to this email. Please make sure the correct images were upload ";
                    }
                }

                mimeMessageHelper.setText(emailBody, true);

                // Sending the mail
                javaMailSender.send(mimeMessage);
            }
            // Catch block to handle MessagingException
            catch (MessagingException e) {
                infoAndDebuglogger.error(e);
            }
        }
    }
}
