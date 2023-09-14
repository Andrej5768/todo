package com.example.todo.registration.listener;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.example.todo.persistence.model.User;
import com.example.todo.registration.OnRegistrationCompleteEvent;
import com.example.todo.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author Andrew
 * @since 09.09.2023
 */
@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    @Autowired
    private IUserService service;

    @Autowired
    private MessageSource messages;

    @Autowired
    private AmazonSimpleEmailService mailSender;

    @Autowired
    private Environment env;

    // API

    @Override
    public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(final OnRegistrationCompleteEvent event) {
        final User user = event.getUser();
        final String token = UUID.randomUUID().toString();
        service.createVerificationTokenForUser(user, token);

        final SendEmailRequest email = constructEmailMessage(event, user, token);
        mailSender.sendEmail(email);
    }

    //

    private SendEmailRequest constructEmailMessage(final OnRegistrationCompleteEvent event, final User user, final String token) {
        final String recipientAddress = user.getEmail();
        final String subject = "Registration Confirmation";
        final String confirmationUrl = event.getAppUrl() + "/registrationConfirm?token=" + token;
        final String message = messages.getMessage("message.regSuccLink", null, "You registered successfully. To confirm your registration, please click on the below link.", event.getLocale());
        final SendEmailRequest email = new SendEmailRequest()
                .withDestination(
                        new Destination().withToAddresses(recipientAddress))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new com.amazonaws.services.simpleemail.model.Content()
                                        .withCharset("UTF-8").withData(message + " \r\n" + "<a href=\"" + confirmationUrl + "\">" + confirmationUrl + "</a>"))
                                .withText(new com.amazonaws.services.simpleemail.model.Content()
                                        .withCharset("UTF-8").withData(message + " \r\n" + confirmationUrl)))
                        .withSubject(new com.amazonaws.services.simpleemail.model.Content()
                                .withCharset("UTF-8").withData(subject)))
                .withSource(env.getProperty("support.email"));
        return email;
    }


}
