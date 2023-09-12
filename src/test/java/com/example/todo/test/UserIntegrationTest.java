package com.example.todo.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.example.todo.persistence.dao.UserRepository;
import com.example.todo.persistence.dao.VerificationTokenRepository;
import com.example.todo.persistence.model.User;
import com.example.todo.persistence.model.VerificationToken;
import com.example.todo.config.TestDbConfig;
import com.example.todo.config.TestIntegrationConfig;
import com.example.todo.validation.EmailExistsException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { TestDbConfig.class, TestIntegrationConfig.class})
@Transactional
public class UserIntegrationTest {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Long tokenId;
    private Long userId;

    //

    @BeforeEach
    public void givenUserAndVerificationToken() throws EmailExistsException {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("SecretPassword");
        user.setFirstName("First");
        user.setLastName("Last");
        entityManager.persist(user);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        entityManager.persist(verificationToken);

        entityManager.flush();
        entityManager.clear();

        tokenId = verificationToken.getId();
        userId = user.getId();
    }

    @AfterEach
    public void flushAfter() {
        entityManager.flush();
        entityManager.clear();
    }

    //

    @Test
    public void whenContextLoad_thenCorrect() {
    	assertTrue(userRepository.count() > 0);
    	assertTrue(tokenRepository.count() > 0);
    }

    @Test
    public void whenRemovingTokenThenUser_thenCorrect() {
        tokenRepository.deleteById(tokenId);
        userRepository.deleteById(userId);
    }

}
