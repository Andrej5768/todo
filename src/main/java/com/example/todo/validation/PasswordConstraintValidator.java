package com.example.todo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

import java.util.Arrays;
import java.util.StringJoiner;

/**
 * @author Andrew
 * @since 09.09.2023
 */
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(final ValidPassword arg0) {

    }

    @Override
    public boolean isValid(final String password, final ConstraintValidatorContext context) {
        // @formatter:off
        final PasswordValidator validator = new PasswordValidator(Arrays.asList(
            new LengthRule(8, 30), 
            new UppercaseCharacterRule(1), 
            new DigitCharacterRule(1), 
            new SpecialCharacterRule(1), 
            new NumericalSequenceRule(3,false),
            new AlphabeticalSequenceRule(3,false),
            new QwertySequenceRule(3,false),
            new WhitespaceRule()));
        final RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        StringJoiner joiner = new StringJoiner(",");
        for (String message : validator.getMessages(result)) {
            joiner.add(message);
        }
        context.buildConstraintViolationWithTemplate(joiner.toString()).addConstraintViolation();
        return false;
    }

}
