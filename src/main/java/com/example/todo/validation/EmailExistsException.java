package com.example.todo.validation;

/**
 * @author Andrew
 * @since 09.09.2023
 */
@SuppressWarnings("serial")
public class EmailExistsException extends Throwable {

    public EmailExistsException(final String message) {
        super(message);
    }

}
