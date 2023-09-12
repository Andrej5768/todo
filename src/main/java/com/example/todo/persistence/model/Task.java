package com.example.todo.persistence.model;

import jakarta.persistence.*;

import java.sql.Timestamp;

/**
 * @author Andrew
 * @since 06.09.2023
 */
@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private String description;

    private boolean done;

    private Timestamp created;

    private Timestamp updated;

    public Task() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getUpdated() {
        return updated;
    }

    public void setUpdated(Timestamp updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        String sb = "Task{" + "id=" + id +
                ", userId=" + userId +
                ", description='" + description + '\'' +
                ", done=" + done +
                ", created=" + created +
                ", updated=" + updated +
                '}';
        return sb;
    }
}
