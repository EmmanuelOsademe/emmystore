package com.emmydev.ecommerce.client.service.user;

import com.emmydev.ecommerce.client.entity.PasswordResetToken;
import com.emmydev.ecommerce.client.entity.Role;
import com.emmydev.ecommerce.client.entity.User;
import com.emmydev.ecommerce.client.entity.VerificationToken;
import com.emmydev.ecommerce.client.enums.ResponseCodes;
import com.emmydev.ecommerce.client.exception.UserAlreadyExistsException;
import com.emmydev.ecommerce.client.exception.UserNotFoundException;
import com.emmydev.ecommerce.client.exception.TokenNotFoundException;
import com.emmydev.ecommerce.client.dto.LoginDto;
import com.emmydev.ecommerce.client.dto.PasswordDto;
import com.emmydev.ecommerce.client.dto.ResponseDto;
import com.emmydev.ecommerce.client.dto.UserDto;
import com.emmydev.ecommerce.client.repository.PasswordResetRepository;
import com.emmydev.ecommerce.client.repository.RoleRepository;
import com.emmydev.ecommerce.client.repository.UserRepository;
import com.emmydev.ecommerce.client.repository.VerificationTokenRepository;
import com.emmydev.ecommerce.client.service.JwtService;
import com.emmydev.ecommerce.client.service.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtService jwtService;


    @Override
    public User registerUser(UserDto userDto) throws UserAlreadyExistsException {

        Optional<User> dbUser = findUserByEmail(userDto.getEmail());
        if(dbUser.isPresent()){
            throw new UserAlreadyExistsException("User with " + userDto.getEmail() + " already exists");
        }

        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        List<Role> roleList = roleRepository.findAll();

        if(roleList.size() == 0){
            Role admin = new Role();
            admin.setRole("ADMIN");
            roleRepository.save(admin);
            user.setEnabled(true);
            user.setRole(admin);
        }else{
            Optional<Role> role = roleRepository.findByRole("USER");
            if(role.isEmpty()){
                Role newRole = new Role();
                newRole.setRole("USER");
                roleRepository.save(newRole);
                user.setRole(newRole);
            }else{
                user.setRole(role.get());
            }
        }

        userRepository.save(user);
        log.info(user.toString());

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
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        log.info(verificationToken.toString());

        // If token not found, return Invalid token
        if(verificationToken.isEmpty()){
            log.error("Invalid token");
            return false;
        }

        // Get the User from the token
        User user = verificationToken.get().getUser();

        // Check if user has been verified
        if(user.isEnabled()){
            return true;
        }

        // Check to be sure that the token has not expired;
        Calendar calendar = Calendar.getInstance();
        if((verificationToken.get().getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0){
            verificationTokenRepository.delete(verificationToken.get());
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
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(oldToken);

        // If the verificationToken does not exist, throw TokenNotFoundException
        if(verificationToken.isEmpty()){
            throw new TokenNotFoundException("Verification token does not exist");
        }

        // Update the verificationToken and save then new token
        VerificationToken token = verificationToken.get();
        token.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(token);
        return token;
    }

    @Override
    public PasswordResetToken resetPassword(String email) throws UserNotFoundException {
        // Verify user
        Optional<User> user = findUserByEmail(email);

        // Throw an error if the user does not exist;
        if(user.isEmpty()){
            throw new UserNotFoundException("User does not exist");
        }

        // Create a password reset token and save it
        String token = UUID.randomUUID().toString();

        // Return the token
        return createPasswordResetToken(user.get(), token);
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
    public User findUserByPasswordResetToken(PasswordResetToken passwordResetToken) throws UserNotFoundException {
        User user = passwordResetToken.getUser();
        if(user == null){
            throw new UserNotFoundException("User does not exist");
        }

        return user;
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public String savePassword(String token, String password) throws TokenNotFoundException, UserNotFoundException {
        // Validate the reset token
        PasswordResetToken passwordResetToken = validatePasswordResetToken(token);

        // Grab the user from the validated token
        User user = findUserByPasswordResetToken(passwordResetToken);

        // Change the password and save to repository

        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        return "Password saved successfully";
    }

    @Override
    public User validateOldPassword(String email, String oldPassword) throws UserNotFoundException {
        // Fetch old user details
        Optional<User> user = userRepository.findByEmail(email);

        if(user.isEmpty()){
            throw new UserNotFoundException("User with " + email + " does not exist");
        }

        User dbUser = user.get();

        // Compare provided password with DbPassword
        String encodedOldPassword = passwordEncoder.encode(oldPassword);
        log.info(encodedOldPassword);
        log.info(dbUser.getPassword());

        boolean isMatch = passwordEncoder.matches(oldPassword, dbUser.getPassword());
        log.info(String.valueOf(isMatch));

        if(!isMatch){
            throw new UserNotFoundException("Invalid credentials");
        }

        return dbUser;
    }

    @Override
    public String updatePassword(PasswordDto passwordDto) throws UserNotFoundException {
        // Validate old password and get the user from the validate password;
        User user = validateOldPassword(passwordDto.getEmail(), passwordDto.getOldPassword());

        // Save the new password;
        changePassword(user, passwordDto.getNewPassword());

        return "Password successfully updated";
    }

    @Override
    public ResponseDto<Object> login(LoginDto loginDetails) {
        log.info(loginDetails.toString());

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDetails.getEmail(), loginDetails.getPassword()));

        log.info("Authentication manager done");
        UserDetails user = userDetailsService.loadUserByUsername(loginDetails.getEmail());
        log.info(user.toString());

        String jwtToken = jwtService.generateToken(user);
        log.info(jwtToken);

        return ResponseDto.builder()
                .responseCode(ResponseCodes.SUCCESS)
                .message("You have successfully logged in")
                .data(jwtToken)
                .build();
    }
}
