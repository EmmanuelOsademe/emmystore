package com.emmydev.ecommerce.client.repository;

import com.emmydev.ecommerce.client.entity.PasswordResetToken;
import com.emmydev.ecommerce.client.entity.Role;
import com.emmydev.ecommerce.client.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PasswordResetRepositoryTest {

    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    PasswordResetToken passwordToken;
    User dbUser;

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

        dbUser = userRepository.save(user);

        PasswordResetToken token = PasswordResetToken.builder()
                .token("token")
                .expirationTime(new Date())
                .user(user)
                .build();

        passwordToken = passwordResetRepository.save(token);
        System.out.println(token);
    }

    @Test
    public void fetchAllTokens(){
        List<PasswordResetToken> tokens = passwordResetRepository.findAll();
        System.out.println("Tokens = " + tokens);
    }

    @Test
    void findByTokenWhenTokenExists() {
        PasswordResetToken token = passwordResetRepository.findByToken("token");
        assertEquals(passwordToken, token);
        System.out.println("Token: " + token);
    }

    @Test
    void findByTokenWhenTokenDoesNotExists() {
        PasswordResetToken token = passwordResetRepository.findByToken("toke");
        assertNull(token);
        System.out.println("Token does not exist");
    }

    @Test
    public void validTokenShouldContainAUser(){
        PasswordResetToken token = passwordResetRepository.findByToken("token");
        System.out.println(token.getUser());
        assertInstanceOf(User.class, token.getUser());
    }
}