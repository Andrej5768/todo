package com.example.todo.service;

import com.example.todo.persistence.model.PasswordResetToken;
import com.example.todo.persistence.model.User;
import com.example.todo.persistence.model.VerificationToken;
import com.example.todo.web.dto.UserDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author Andrew
 * @since 08.09.2023
 */
public interface IUserService {

    User registerNewUserAccount(UserDto accountDto);

    User getUser(String verificationToken);

    void saveRegisteredUser(User user);

    void deleteUser(User user);

    void createVerificationTokenForUser(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    VerificationToken generateNewVerificationToken(String token);

    void createPasswordResetTokenForUser(User user, String token);

    User findUserByEmail(String email);

    PasswordResetToken getPasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    Optional<User> getUserByID(long id);

    void changeUserPassword(User user, String password);

    boolean checkIfValidOldPassword(User user, String password);

    String validateVerificationToken(String token);

    List<String> getUsersFromSessionRegistry();

    ResponseEntity<?> getToken(User user);
}
