package com.emmydev.ecommerce.client.repository;

import com.emmydev.ecommerce.client.entity.Role;
import com.emmydev.ecommerce.client.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void saveUser(){
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
    }


    @Test
    public void fetchAllUsers(){
        List<User> users = userRepository.findAll();
        System.out.println("users = " + users);
    }


    @Test
    public void findByUserEmailWhenEmailExists() {
        Optional<User> user = userRepository.findByEmail("emma.osademe@gmail.com");
        assertTrue(user.isPresent());
        System.out.println("user = " + user.get());
    }

    @Test
    public void findUserByEmailWhenEmailDoesNotExist() {
        Optional<User> user = userRepository.findByEmail("emm.osademe@gmail.com");
        assertTrue(user.isEmpty());
        System.out.println("user does not exist" );
    }
}