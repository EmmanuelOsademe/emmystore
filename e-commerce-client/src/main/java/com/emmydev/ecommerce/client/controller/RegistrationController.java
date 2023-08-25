package com.emmydev.ecommerce.client.controller;

import com.emmydev.ecommerce.client.entity.PasswordResetToken;
import com.emmydev.ecommerce.client.entity.User;
import com.emmydev.ecommerce.client.entity.VerificationToken;
import com.emmydev.ecommerce.client.event.RegistrationCompleteEvent;
import com.emmydev.ecommerce.client.exception.UserAlreadyExistsException;
import com.emmydev.ecommerce.client.exception.UserNotFoundException;
import com.emmydev.ecommerce.client.exception.TokenNotFoundException;
import com.emmydev.ecommerce.client.dto.LoginDto;
import com.emmydev.ecommerce.client.dto.PasswordDto;
import com.emmydev.ecommerce.client.dto.ResponseDto;
import com.emmydev.ecommerce.client.dto.UserDto;
import com.emmydev.ecommerce.client.service.user.UserService;
import com.emmydev.ecommerce.client.service.email.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Objects;

@Slf4j
@RestController
public class RegistrationController {
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostMapping("/user")
    public String registerUser(@Valid @RequestBody UserDto userDto, final HttpServletRequest request) throws UserAlreadyExistsException {
        User user = userService.registerUser(userDto);

        publisher.publishEvent(new RegistrationCompleteEvent(user, getApplicationUrl(request)));

        if(Objects.equals(user.getRole().getRole(), "ADMIN")){
            return "You account has been registered as admin";
        }
        return "Your account has been registered.\nPlease check your mail to verify your account";
    }

    @GetMapping("new-user-verify")
    public String verifyRegistration(@RequestParam("token") String token){
        boolean isVerified = userService.validateRegistrationToken(token);
        if(isVerified){
            log.info("Success");
            return "Your account has been successfully verified";
        }
        return "Your account could not be verified";
    }

    @GetMapping("new-verification-token")
    public String resendVerificationToken(@RequestParam("token") String token, final HttpServletRequest request) throws TokenNotFoundException {
        //Receive the verificationToken form the userService
        VerificationToken verificationToken = userService.generateNewVerificationToken(token);

        // Receive application url from the request
        String applicationUrl = getApplicationUrl(request);

        // Send the email notification
        resendVerificationTokenMail(verificationToken, applicationUrl);

        return "A reverification link has been sent to your email. Please check your email to verify your account";
    }

    @PostMapping("password-reset-token")
    public String resetPassword(@RequestBody PasswordDto passwordDto, final HttpServletRequest request) throws UserNotFoundException {
        // Get the password token from the resetPassword service
        PasswordResetToken passwordResetToken = userService.resetPassword(passwordDto.getEmail());

        // Get the application url from the request
        String applicationUrl = getApplicationUrl(request);

        // Send a password reset mail to the use
        passwordResetTokenMail(passwordResetToken, applicationUrl);

        return "A password reset link has been sent to your mail. Please check your mail and reset your password";
    }

    @PostMapping("new-password-save")
    public String savePassword(@RequestParam("token") String resetToken, @RequestBody String password) throws TokenNotFoundException, UserNotFoundException {
        // Get the message from the savePassword service
        String successMessage = userService.savePassword(resetToken, password);
        return successMessage;
    }

    @PostMapping("new-password-update")
    public String updatePassword(@RequestBody PasswordDto passwordDto) throws UserNotFoundException {
        // Get message from the updatePassword service
        String successMessage = userService.updatePassword(passwordDto);
        return successMessage;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<Object>> login(@Valid @RequestBody LoginDto loginData){
        return ResponseEntity.ok(userService.login(loginData));
    }

    private String getApplicationUrl(HttpServletRequest request){
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    private void resendVerificationTokenMail(VerificationToken verificationToken, String applicationUrl){
        // Get the user and token from the verification token object and use the user detail to send a verification mail
        User user = verificationToken.getUser();
        String newToken = verificationToken.getToken();

        String url = applicationUrl + "/verifyRegistration?token=" + newToken;
        String messageBody = "Dear " + user.getFirstName() + "\n\nPlease click on this link to verify your account: " + url;
        log.info(messageBody);
        emailService.sendSimpleMessage("emmyshoppinghub@gmail.com", user.getEmail(), "Account Reverification", messageBody);
    }

    private void passwordResetTokenMail(PasswordResetToken passwordResetToken, String applicationUrl){
        // Get the user and the token
        User user = passwordResetToken.getUser();
        String token = passwordResetToken.getToken();

        // Generate the application url and the message
        String url = applicationUrl + "/savePassword?token=" + token;
        String messageBody = "Dear " + user.getFirstName() + ",\n\nPlease click this link to reset your password: " + url;

        log.info(messageBody);
        emailService.sendSimpleMessage("emmyshoppinghub@gmail.com", user.getEmail(), "Password Reset", messageBody);
    }
}
