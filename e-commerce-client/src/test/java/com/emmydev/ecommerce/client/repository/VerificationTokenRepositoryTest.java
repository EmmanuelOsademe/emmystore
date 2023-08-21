package com.emmydev.ecommerce.client.repository;

import com.emmydev.ecommerce.client.entity.PasswordResetToken;
import com.emmydev.ecommerce.client.entity.Role;
import com.emmydev.ecommerce.client.entity.User;
import com.emmydev.ecommerce.client.entity.VerificationToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VerificationTokenRepositoryTest {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    VerificationToken verificationToken;

    @Test
    public void saveToken(){
        Role role = Role.builder()
                .role("ADMIN")
                .build();

        User user = User.builder()
                .firstName("Emmanuel")
                .lastName("Osademe")
                .email("emma.osademe@gmail.com")
                .password(passwordEncoder.encode("password"))
                .role(role)
                .build();

        userRepository.save(user);

        VerificationToken token = VerificationToken.builder()
                .token("token")
                .expirationTime(new Date())
                .user(user)
                .build();

        verificationToken =verificationTokenRepository.save(token);
        System.out.println(token);
    }

    @Test
    public void fetchAllTokens(){
        List<VerificationToken> tokens = verificationTokenRepository.findAll();
        System.out.println("Tokens = " + tokens);
    }

    @Test
    public void findByTokenWhenTokenExists() {
        VerificationToken token = verificationTokenRepository.findByToken("token");
        assertEquals(verificationToken, token);
        System.out.println("Token: " + token);
    }

    @Test
    public void findByTokenWhenTokenDoesNotExists() {
        VerificationToken token = verificationTokenRepository.findByToken("toke");
        assertNull(token);
        System.out.println("Token does not exist");
    }

    @Test
    public void validTokenShouldContainAUser(){
        VerificationToken token = verificationTokenRepository.findByToken("token");
        System.out.println(token.getUser());
        assertInstanceOf(User.class, token.getUser());
    }
}