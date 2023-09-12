package com.example.todo.persistence.dao;


import com.example.todo.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Andrew
 * @since 07.09.2023
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Override
    void delete(User user);

}
