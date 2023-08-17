package com.emmydev.ecommerce.client.service.user;

import com.emmydev.ecommerce.client.entity.PasswordResetToken;
import com.emmydev.ecommerce.client.entity.User;
import com.emmydev.ecommerce.client.entity.VerificationToken;
import com.emmydev.ecommerce.client.exception.UserAlreadyExistsException;
import com.emmydev.ecommerce.client.exception.UserNotFoundException;
import com.emmydev.ecommerce.client.exception.TokenNotFoundException;
import com.emmydev.ecommerce.client.model.LoginModel;
import com.emmydev.ecommerce.client.model.PasswordModel;
import com.emmydev.ecommerce.client.model.ResponseModel;
import com.emmydev.ecommerce.client.model.UserModel;

import java.util.Optional;

public interface UserService {
    User registerUser(UserModel userModel) throws UserAlreadyExistsException;

    Optional<User> findUserByEmail(String email);

    void saveVerificationToken(String token, User user);

    boolean validateRegistrationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken) throws TokenNotFoundException;

    PasswordResetToken createPasswordResetToken(User user, String token);

    PasswordResetToken resetPassword(String email) throws UserNotFoundException;

     Optional<User> findUserByPasswordResetToken(PasswordResetToken passwordResetToken);

    PasswordResetToken validatePasswordResetToken(String token) throws TokenNotFoundException;

    void changePassword(User user, String newPassword);

    String savePassword(String token, String password) throws TokenNotFoundException;

    User validateOldPassword(String email, String oldPassword) throws UserNotFoundException;

    String updatePassword(PasswordModel passwordModel) throws UserNotFoundException;

    ResponseModel<Object> login(LoginModel loginDetails);
}
