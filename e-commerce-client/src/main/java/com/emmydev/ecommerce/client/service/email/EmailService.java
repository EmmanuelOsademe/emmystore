package com.emmydev.ecommerce.client.service.email;

import javax.mail.MessagingException;

public interface EmailService {
    void sendSimpleMessage(String from, String to, String subject, String text);

    void sendMessageWithAttachment(String from, String to, String text, String subject, String pathToAttachment) throws MessagingException;
}
