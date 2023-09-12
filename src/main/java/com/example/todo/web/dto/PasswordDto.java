package com.example.todo.web.dto;

import com.example.todo.validation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Andrew
 * @since 09.09.2023
 */
@Schema(name = "PasswordDto", description = "DTO class for password reset")
public class PasswordDto {

    @Schema(description = "User's old password", required = true)
    private String oldPassword;

    @Schema(description = "Token from email", required = true)
    private String token;

    @ValidPassword
    @Schema(description = "User's new password", required = true)
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
