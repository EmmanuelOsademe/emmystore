package com.emmydev.ecommerce.client.service.user;

import com.emmydev.ecommerce.client.entity.PasswordResetToken;
import com.emmydev.ecommerce.client.entity.User;
import com.emmydev.ecommerce.client.entity.VerificationToken;
import com.emmydev.ecommerce.client.exception.UserAlreadyExistsException;
import com.emmydev.ecommerce.client.exception.UserNotFoundException;
import com.emmydev.ecommerce.client.exception.TokenNotFoundException;
import com.emmydev.ecommerce.client.dto.LoginDto;
import com.emmydev.ecommerce.client.dto.PasswordDto;
import com.emmydev.ecommerce.client.dto.ResponseDto;
import com.emmydev.ecommerce.client.dto.UserDto;

import java.util.Optional;

public interface UserService {
    User registerUser(UserDto userDto) throws UserAlreadyExistsException;

    Optional<User> findUserByEmail(String email);

    void saveVerificationToken(String token, User user);

    boolean validateRegistrationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken) throws TokenNotFoundException;

    PasswordResetToken createPasswordResetToken(User user, String token);

    PasswordResetToken resetPassword(String email) throws UserNotFoundException;

     User findUserByPasswordResetToken(PasswordResetToken passwordResetToken) throws UserNotFoundException;

    PasswordResetToken validatePasswordResetToken(String token) throws TokenNotFoundException;

    void changePassword(User user, String newPassword);

    String savePassword(String token, String password) throws TokenNotFoundException, UserNotFoundException;

    User validateOldPassword(String email, String oldPassword) throws UserNotFoundException;

    String updatePassword(PasswordDto passwordDto) throws UserNotFoundException;

    ResponseDto<Object> login(LoginDto loginDetails);
}
