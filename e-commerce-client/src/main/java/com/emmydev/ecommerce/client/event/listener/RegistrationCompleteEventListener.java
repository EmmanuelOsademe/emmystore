package com.emmydev.ecommerce.client.event.listener;

import com.emmydev.ecommerce.client.entity.User;
import com.emmydev.ecommerce.client.event.RegistrationCompleteEvent;
import com.emmydev.ecommerce.client.service.user.UserService;
import com.emmydev.ecommerce.client.service.email.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // Get the user object from the event and generate a random token
        User user = event.getUser();
        String token = UUID.randomUUID().toString();

        // Save the verification token
        userService.saveVerificationToken(token, user);

        // Send a registration notification mail to the User
        String url = event.getApplicationUrl() + "/verifyRegistration?token=" + token;

        // Message body and message header;
        String messageBody;
        String messageHeader;
        if(user.getRole().getRole() == "ADMIN"){
            messageBody = "Dear " + user.getFirstName() + ",\n\nYour account has been successfully registered with admin privilege";
            messageHeader = "Registration Complete";
        }else{
            messageBody = "Dear " + user.getFirstName() +"\n\nThank you for registration at EmmysDev. \n\nClick this link to verify your account: " + url;
            messageHeader = "Complete your registration";
        }

        emailService.sendSimpleMessage("emmyshoppinghub@gmail.com", user.getEmail(), messageHeader, messageBody);
    }
}
