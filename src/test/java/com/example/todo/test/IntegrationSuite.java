package com.example.todo.test;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectClasses({ // @formatter:off
    ChangePasswordIntegrationTest.class,
    TokenExpirationIntegrationTest.class,
    RegistrationControllerIntegrationTest.class,
    GetLoggedUsersIntegrationTest.class,
    UserServiceIntegrationTest.class,
    UserIntegrationTest.class,
    SpringSecurityRolesIntegrationTest.class,
    TaskServiceIntegrationTest.class,
    TaskRestControllerTest.class,
})// @formatter:on
public class IntegrationSuite {
  //
}
