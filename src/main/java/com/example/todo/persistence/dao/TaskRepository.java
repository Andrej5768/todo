package com.example.todo.persistence.dao;


import com.example.todo.persistence.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Andrew
 * @since 06.09.2023
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByUserId(Long userId);
}
