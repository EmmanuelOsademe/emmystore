package com.emmydev.ecommerce.client.dto;

import com.emmydev.ecommerce.client.validation.PasswordMatches;
import com.emmydev.ecommerce.client.validation.ValidEmail;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@PasswordMatches
@Builder
public class UserDto {

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @ValidEmail
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    @NotBlank(message = "Password confirmation cannot be blank")
    private String passwordConfirmation;
}
