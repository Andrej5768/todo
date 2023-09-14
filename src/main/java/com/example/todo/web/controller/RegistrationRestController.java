package com.example.todo.web.controller;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.example.todo.persistence.model.User;
import com.example.todo.persistence.model.VerificationToken;
import com.example.todo.registration.OnRegistrationCompleteEvent;
import com.example.todo.security.ISecurityUserService;
import com.example.todo.service.IUserService;
import com.example.todo.web.dto.PasswordDto;
import com.example.todo.web.dto.UserDto;
import com.example.todo.web.util.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Andrew
 * @since 08.09.2023
 */
@RestController
@RequestMapping("/api")
public class RegistrationRestController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private IUserService userService;

    @Autowired
    private ISecurityUserService securityUserService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private AmazonSimpleEmailService mailSender;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private Environment env;

    public RegistrationRestController() {
        super();
    }

    // Registration
    @Operation(summary = "Register user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully registered user account",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PostMapping("/user/registration")
    public GenericResponse registerUserAccount(@Valid final UserDto accountDto, final HttpServletRequest request) {
        LOGGER.debug("Registering user account with information: {}", accountDto);
        System.out.println("Registering user account with information: {}" + accountDto);
        final User registered = userService.registerNewUserAccount(accountDto);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
        return new GenericResponse("success");
    }

    // User activation - verification
    @Operation(summary = "Resend registration token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully resent registration token",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/user/resendRegistrationToken")
    public GenericResponse resendRegistrationToken(final HttpServletRequest request, @RequestParam("token") final String existingToken) {
        final VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
        final User user = userService.getUser(newToken.getToken());
        mailSender.sendEmail(constructResendVerificationTokenEmail(getAppUrl(request), request.getLocale(), newToken, user));
        return new GenericResponse(messages.getMessage("message.resendToken", null, request.getLocale()));
    }

    // Reset password
    @Operation(summary = "Reset password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully reset password",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PostMapping("/user/resetPassword")
    public GenericResponse resetPassword(final HttpServletRequest request, @RequestParam("email") final String userEmail) {
        final User user = userService.findUserByEmail(userEmail);
        if (user != null) {
            final String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            mailSender.sendEmail(constructResetTokenEmail(getAppUrl(request), request.getLocale(), token, user));
        }
        return new GenericResponse(messages.getMessage("message.resetPasswordEmail", null, request.getLocale()));
    }

    // Save password
    @Operation(summary = "Save password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully saved password",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PostMapping("/user/savePassword")
    public GenericResponse savePassword(final Locale locale, @Valid PasswordDto passwordDto) {

        final String result = securityUserService.validatePasswordResetToken(passwordDto.getToken());

        if (result != null) {
            return new GenericResponse(messages.getMessage("auth.message." + result, null, locale));
        }

        Optional<User> user = userService.getUserByPasswordResetToken(passwordDto.getToken());
        if (user.isPresent()) {
            userService.changeUserPassword(user.get(), passwordDto.getNewPassword());
            return new GenericResponse(messages.getMessage("message.resetPasswordSuc", null, locale));
        } else {
            return new GenericResponse(messages.getMessage("auth.message.invalid", null, locale));
        }
    }

    // Change user password
    @Operation(summary = "Change user password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully changed user password",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PostMapping("/user/updatePassword")
    public ResponseEntity<?> changeUserPassword(final Locale locale, @Valid PasswordDto passwordDto) {
        String username = ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        final User user = userService.findUserByEmail(username);
        if (!userService.checkIfValidOldPassword(user, passwordDto.getOldPassword())) {
            return ResponseEntity.badRequest().body(new GenericResponse(messages.getMessage("message.invalidOldPassword", null, locale)));
        }
        userService.changeUserPassword(user, passwordDto.getNewPassword());
        return ResponseEntity.ok(new GenericResponse(messages.getMessage("message.updatePasswordSuc", null, locale)));
    }

    // ============== NON-API ============

    private SendEmailRequest constructResendVerificationTokenEmail(final String contextPath, final Locale locale, final VerificationToken newToken, final User user) {
        final String confirmationUrl = contextPath + "/registrationConfirm.html?token=" + newToken.getToken();
        final String message = messages.getMessage("message.resendToken", null, locale);
        return constructEmail("Resend Registration Token", message + " \r\n" + confirmationUrl, user);
    }

    private SendEmailRequest constructResetTokenEmail(final String contextPath, final Locale locale, final String token, final User user) {
        final String url = contextPath + "/user/changePassword?token=" + token;
        final String message = messages.getMessage("message.resetPassword", null, locale);
        return constructEmail("Reset Password", message + " \r\n" + url, user);
    }

    private SendEmailRequest constructEmail(String subject, String body, User user) {
        final SendEmailRequest email = new SendEmailRequest()
                .withDestination(
                        new Destination().withToAddresses(user.getEmail()))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new com.amazonaws.services.simpleemail.model.Content()
                                        .withCharset("UTF-8").withData(body))
                                .withText(new com.amazonaws.services.simpleemail.model.Content()
                                        .withCharset("UTF-8").withData(body)))
                        .withSubject(new com.amazonaws.services.simpleemail.model.Content()
                                .withCharset("UTF-8").withData(subject)))
                .withSource(env.getProperty("support.email"));
        return email;
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
