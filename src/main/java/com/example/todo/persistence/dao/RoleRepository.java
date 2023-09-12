package com.example.todo.persistence.dao;


import com.example.todo.persistence.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Andrew
 * @since 07.09.2023
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

    @Override
    void delete(Role role);

}
