package com.example.todo.security;

import com.example.todo.persistence.dao.PasswordResetTokenRepository;
import com.example.todo.persistence.model.PasswordResetToken;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

/**
 * @author Andrew
 * @since 09.09.2023
 */
@Service
@Transactional
public class UserSecurityService implements ISecurityUserService {

    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;

    @Override
    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);

        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }
}