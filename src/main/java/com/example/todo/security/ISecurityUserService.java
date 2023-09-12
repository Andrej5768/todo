package com.example.todo.security;

/**
 * @author Andrew
 * @since 09.09.2023
 */
public interface ISecurityUserService {

    String validatePasswordResetToken(String token);

}
