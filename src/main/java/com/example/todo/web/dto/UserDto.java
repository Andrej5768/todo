package com.example.todo.web.dto;

import com.example.todo.validation.PasswordMatches;
import com.example.todo.validation.ValidEmail;
import com.example.todo.validation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * @author Andrew
 * @since 10.09.2023
 */
@PasswordMatches
@Schema(name = "UserDto", description = "DTO class for user")
public class UserDto {
    @NotNull
    @Size(min = 1, message = "{Size.userDto.firstName}")
    @Schema(description = "User's first name", required = true)
    private String firstName;

    @NotNull
    @Size(min = 1, message = "{Size.userDto.lastName}")
    @Schema(description = "User's last name", required = true)
    private String lastName;

    @ValidPassword
    @Schema(description = "User's password", required = true)
    private String password;

    @NotNull
    @Size(min = 1)
    @Schema(description = "User's matching password", required = true)
    private String matchingPassword;

    @ValidEmail
    @NotNull
    @Size(min = 1, message = "{Size.userDto.email}")
    @Schema(description = "User's email", required = true)
    private String email;
    private Integer role;

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(final Integer role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(final String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    @Override
    public String toString() {
        String builder = "UserDto [firstName=" +
                firstName +
                ", lastName=" +
                lastName +
                ", email=" +
                email +
                ", role=" +
                role + "]";
        return builder;
    }

}
