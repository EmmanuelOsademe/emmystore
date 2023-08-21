package com.emmydev.ecommerce.client.service.user;

import com.emmydev.ecommerce.client.dto.UserDto;
import com.emmydev.ecommerce.client.entity.Role;
import com.emmydev.ecommerce.client.entity.User;
import com.emmydev.ecommerce.client.exception.UserAlreadyExistsException;
import com.emmydev.ecommerce.client.repository.RoleRepository;
import com.emmydev.ecommerce.client.repository.UserRepository;
import com.emmydev.ecommerce.client.repository.VerificationTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    VerificationTokenRepository verificationTokenRepository;

    @BeforeEach
    public void setUp() {
        Role role = Role.builder()
                .role("ADMIN")
                .build();

        User user = User.builder()
                .firstName("Emmanuel")
                .lastName("Osademe")
                .email("emma.osademe@gmail.com")
                .password(passwordEncoder.encode("password"))
                .role(role)
                .id(1L)
                .build();

        Mockito.when(userRepository.findByEmail("emma.osademe@gmail.com"))
                .thenReturn(Optional.ofNullable(user));
    }

    @Test
    void registerUserShouldCreateUserIfUserDoesNotExist() throws UserAlreadyExistsException {
        UserDto userDto = UserDto.builder()
                .firstName("Emmanuel")
                .lastName("Osademe")
                .email("emm.osademe@gmail.com")
                .password("password")
                .passwordConfirmation("password")
                .build();

        User user = userService.registerUser(userDto);
        log.info(user.toString());

        assertInstanceOf(User.class, user);
    }

    @Test
    @DisplayName("Return null if user email does not exist")
    public void findUserByEmailShouldNotReturnUserIfEmailDoesNotExist(){
        Optional<User> user = userService.findUserByEmail("emmy@gmail.com");
        assertTrue(user.isEmpty());
    }

    @Test
    public void findUserByEmailShouldNotReturnUserIfUserExists(){
        Optional<User> user = userService.findUserByEmail("emma.osademe@gmail.com");
        assertTrue(user.isPresent());
    }

//
//    @Test
//    void saveVerificationToken() {
//    }
//
//    @Test
//    void validateRegistrationToken() {
//    }
//
//    @Test
//    void generateNewVerificationToken() {
//    }
//
//    @Test
//    void createPasswordResetToken() {
//    }
//
//    @Test
//    void resetPassword() {
//    }
//
//    @Test
//    void findUserByPasswordResetToken() {
//    }
//
//    @Test
//    void validatePasswordResetToken() {
//    }
//
//    @Test
//    void changePassword() {
//    }
//
//    @Test
//    void savePassword() {
//    }
//
//    @Test
//    void validateOldPassword() {
//    }
//
//    @Test
//    void updatePassword() {
//    }
//
//    @Test
//    void login() {
//    }
}