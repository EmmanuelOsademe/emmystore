package com.emmydev.ecommerce.client.service.user;

import com.emmydev.ecommerce.client.entity.PasswordResetToken;
import com.emmydev.ecommerce.client.entity.Role;
import com.emmydev.ecommerce.client.entity.User;
import com.emmydev.ecommerce.client.entity.VerificationToken;
import com.emmydev.ecommerce.client.enums.ResponseCodes;
import com.emmydev.ecommerce.client.exception.UserAlreadyExistsException;
import com.emmydev.ecommerce.client.exception.UserNotFoundException;
import com.emmydev.ecommerce.client.exception.TokenNotFoundException;
import com.emmydev.ecommerce.client.model.LoginModel;
import com.emmydev.ecommerce.client.model.PasswordModel;
import com.emmydev.ecommerce.client.model.ResponseModel;
import com.emmydev.ecommerce.client.model.UserModel;
import com.emmydev.ecommerce.client.repository.PasswordResetRepository;
import com.emmydev.ecommerce.client.repository.RoleRepository;
import com.emmydev.ecommerce.client.repository.UserRepository;
import com.emmydev.ecommerce.client.repository.VerificationTokenRepository;
import com.emmydev.ecommerce.client.service.JwtService;
import com.emmydev.ecommerce.client.service.UserDetailsServiceImpl;
import com.emmydev.ecommerce.client.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private VerificationTokenRepository verificationTokenRepository;

    private RoleRepository roleRepository;

    private PasswordResetRepository passwordResetRepository;

    private PasswordEncoder passwordEncoder;

    private AuthenticationManager authenticationManager;

    private UserDetailsServiceImpl userDetailsService;

    private JwtService jwtService;


    @Override
    public User registerUser(UserModel userModel) throws UserAlreadyExistsException {

        Optional<User> dbUser = findUserByEmail(userModel.getEmail());
        if(dbUser.isPresent()){
            throw new UserAlreadyExistsException("User with " + userModel.getEmail() + " already exists");
        }

        User user = new User();
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setEmail(userModel.getEmail());
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));

        List<Role> roleList = roleRepository.findAll();

        if(roleList.size() == 0){
            Role admin = new Role();
            admin.setRole("ADMIN");
            roleRepository.save(admin);
            user.isEnabled();
            user.setRole(admin);
        }else{
            Optional<Role> role = roleRepository.findByRole("USER");
            if(!role.isPresent()){
                Role newRole = new Role();
                newRole.setRole("USER");
                roleRepository.save(newRole);
                user.setRole(newRole);
            }else{
                user.setRole(role.get());
            }
        }

        userRepository.save(user);

        return user;
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveVerificationToken(String token, User user) {
        VerificationToken verificationToken = new VerificationToken(token, user);

        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public boolean validateRegistrationToken(String token) {
        // Attempt to get the verification token from the database
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        log.info(verificationToken.toString());

        // If token not found, return Invalid token
        if(verificationToken == null){
            log.error("Invalid token");
            return false;
        }

        // Get the User from the token
        User user = verificationToken.getUser();

        // Check if user has been verified
        if(user.isEnabled()){
            return true;
        }

        // Check to be sure that the token has not expired;
        Calendar calendar = Calendar.getInstance();
        if((verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0){
            verificationTokenRepository.delete(verificationToken);
            log.error("Token has expired");
            return false;
        }

        // Enable the user and save the updated user
        user.setEnabled(true);
        userRepository.save(user);
        return true;
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) throws TokenNotFoundException {
        // Get existing verificationToken object using the token
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);

        // If the verificationToken does not exist, throw TokenNotFoundException
        if(verificationToken == null){
            throw new TokenNotFoundException("Verification token does not exist");
        }

        // Update the verificationToken and save then new token
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    @Override
    public PasswordResetToken resetPassword(String email) throws UserNotFoundException {
        // Verify user
        Optional<User> user = findUserByEmail(email);

        // Throw an error if the user does not exist;
        if(!user.isPresent()){
            throw new UserNotFoundException("User does not exist");
        }

        // Create a password reset token and save it
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = createPasswordResetToken(user.get(), token);

        // Return the token
        return resetToken;
    }

    @Override
    public PasswordResetToken createPasswordResetToken(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);
        passwordResetRepository.save(passwordResetToken);

        return passwordResetToken;
    }

    @Override
    public PasswordResetToken validatePasswordResetToken(String token) throws TokenNotFoundException {
        PasswordResetToken resetToken = passwordResetRepository.findByToken(token);

        if(resetToken == null){
            throw new TokenNotFoundException("Invalid token provided");
        }

        Calendar cal = Calendar.getInstance();
        if((resetToken.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0){
            passwordResetRepository.delete(resetToken);
            throw new TokenNotFoundException("Token expired");
        }

        return resetToken;
    }

    @Override
    public Optional<User> findUserByPasswordResetToken(PasswordResetToken passwordResetToken) {
        return Optional.ofNullable(passwordResetToken.getUser());
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public String savePassword(String token, String password) throws TokenNotFoundException {
        // Validate the reset token
        PasswordResetToken passwordResetToken = validatePasswordResetToken(token);

        // Grab the user from the validated token
        Optional<User> user = findUserByPasswordResetToken(passwordResetToken);

        if(!user.isPresent()){
            throw new TokenNotFoundException("Token could not be matched to a user");
        }

        // Change the password and save to repository
        changePassword(user.get(), password);

        return "Password saved successfully";
    }

    @Override
    public User validateOldPassword(String email, String oldPassword) throws UserNotFoundException {
        // Fetch old user details
        User user = userRepository.findByEmail(email).get();

        // Compare provided password with DbPassword
        String encodedOldPassword = passwordEncoder.encode(oldPassword);
        log.info(encodedOldPassword);
        log.info(user.getPassword());

        boolean isMatch = passwordEncoder.matches(oldPassword, user.getPassword());
        log.info(String.valueOf(isMatch));

        if(!isMatch){
            throw new UserNotFoundException("Invalid credentials");
        }

        return user;
    }

    @Override
    public String updatePassword(PasswordModel passwordModel) throws UserNotFoundException {
        // Validate old password and get the user from the validate password;
        User user = validateOldPassword(passwordModel.getEmail(), passwordModel.getOldPassword());

        // Save the new password;
        changePassword(user, passwordModel.getNewPassword());

        return "Password successfully updated";
    }

    @Override
    public ResponseModel<Object> login(LoginModel loginDetails) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDetails.getEmail(), loginDetails.getPassword()));

        UserDetails user = userDetailsService.loadUserByUsername(loginDetails.getEmail());

        String jwtToken = jwtService.generateToken(user);

        return ResponseModel.builder()
                .responseCode(ResponseCodes.SUCCESS)
                .message("You have successfully logged in")
                .data(jwtToken)
                .build();
    }
}