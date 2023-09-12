package com.example.todo.persistence.dao;

import com.example.todo.persistence.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Andrew
 * @since 07.09.2023
 */
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    Privilege findByName(String name);

    @Override
    void delete(Privilege privilege);

}
