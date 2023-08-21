//package com.emmydev.ecommerce.client.controller;
//
//import com.emmydev.ecommerce.client.config.JwtAuthenticationFilter;
//import com.emmydev.ecommerce.client.dto.UserDto;
//import com.emmydev.ecommerce.client.entity.Role;
//import com.emmydev.ecommerce.client.entity.User;
//import com.emmydev.ecommerce.client.exception.UserAlreadyExistsException;
//import com.emmydev.ecommerce.client.service.email.EmailService;
//import com.emmydev.ecommerce.client.service.user.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(RegistrationController.class)
//class RegistrationControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private JwtAuthenticationFilter authenticationFilter;
//
//    @Autowired
//    private EmailService emailService;
//
//    @Autowired
//    private ApplicationEventPublisher publisher;
//
//    @MockBean
//    private UserService userService;
//
//    private User user;
//
//    @BeforeEach
//    void setUp(){
//        Role role = Role.builder()
//                .role("ADMIN")
//                .roleId(1L)
//                .build();
//
//        user = User.builder()
//                .firstName("Emmanuel")
//                .lastName("Osademe")
//                .email("emma.osademe@gmail.com")
//                .password(passwordEncoder.encode("password"))
//                .id(1L)
//                .role(role)
//                .enabled(true)
//                .build();
//    }
//
//    @Test
//    void registerUser() throws Exception {
//        UserDto userDto = UserDto
//                .builder()
//                .firstName("Emmanuel")
//                .lastName("Osademe")
//                .email("emma.osademe@gmail.com")
//                .password("password")
//                .passwordConfirmation("password")
//                .build();
//
//        Mockito.when(userService.registerUser(userDto))
//                .thenReturn(user);
//
//        mockMvc.perform(post("/user")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\n" +
//                        "    \"firstName\": \"Emmanuel\",\n" +
//                        "    \"lastName\": \"Osademe\",\n" +
//                        "    \"email\": \"emma.osademe@gmail.com\",\n" +
//                        "    \"password\": \"password\",\n" +
//                        "    \"passwordConfirmation\": \"password\"\n" +
//                        "}")
//                ).andExpect(status().isOk());
//    }
//
//    @Test
//    void verifyRegistration() {
//    }
//}