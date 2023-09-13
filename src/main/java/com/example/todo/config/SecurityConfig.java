package com.example.todo.config;

import com.example.todo.persistence.dao.UserRepository;
import com.example.todo.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 * @author Andrew
 * @since 08.09.2023
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Autowired
    private CustomWebAuthenticationDetailsSource authenticationDetailsSource;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public SecurityConfig(
            JwtAuthorizationFilter jwtAuthorizationFilter) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http


                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers(
                                mvc.pattern("/login*"),
                                mvc.pattern("/logout*"),
                                mvc.pattern("/badUser*"),
                                mvc.pattern("/emailError*"),
                                mvc.pattern("/registration*"),
                                mvc.pattern("/successRegister*"),
                                mvc.pattern("/updatePassword*"),
                                mvc.pattern("/user/changePassword*"),
                                mvc.pattern("/user/forgetPassword*"),
                                mvc.pattern("/user/resendRegistrationToken*"),
                                mvc.pattern("/user/resetPassword*"),
                                mvc.pattern("/api/user/login*"),
                                mvc.pattern("/api/user/registration*"),
                                mvc.pattern("/js/**"),
                                mvc.pattern("/css/**")
                        ).permitAll()
                        .requestMatchers(
                                mvc.pattern("/v2/api-docs"),
                                mvc.pattern("/swagger-resources"),
                                mvc.pattern("/swagger-resources/**"),
                                mvc.pattern("/configuration/ui"),
                                mvc.pattern("/configuration/security"),
                                mvc.pattern("/swagger-ui.html"),
                                mvc.pattern("/webjars/**"),
                                mvc.pattern("/v3/api-docs/**"),
                                mvc.pattern("/swagger-ui/**")
                        ).permitAll()
                        .requestMatchers(mvc.pattern("/invalidSession*")).anonymous()
                        .requestMatchers(mvc.pattern("/api/user/updatePassword*")
                        ).hasAuthority("CHANGE_PASSWORD_PRIVILEGE")
                        .requestMatchers(
                                mvc.pattern("/loggedUsers*"),
                                mvc.pattern("/console*")
                        ).hasAuthority("READ_PRIVILEGE")
                        .anyRequest().hasAuthority("READ_PRIVILEGE"))
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(myAuthenticationSuccessHandler())
                        .failureHandler(myAuthenticationFailureHandler())
                        .authenticationDetailsSource(authenticationDetailsSource)
                        .defaultSuccessUrl("/tasks/", true)
                        .permitAll())
                .rememberMe((rememberMe) -> rememberMe
                        .key("remember-me")
                        .rememberMeServices(rememberMeServices())
                        .tokenValiditySeconds(86400))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl("/invalidSession.html")
                        .maximumSessions(1)
                        .sessionRegistry(sessionRegistry())
                )
                .logout(logout -> logout
                        .logoutUrl("/logout.html")
                        .logoutSuccessUrl("/logout?logSucc=true")
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .addFilter(jwtAuthorizationFilter)
                .userDetailsService(userDetailsService)
                .csrf(AbstractHttpConfigurer::disable
                );
        return http.build();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public AuthenticationSuccessHandler myAuthenticationSuccessHandler() {
        return new MyCustomLoginAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler myAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        CustomRememberMeServices rememberMeServices = new CustomRememberMeServices("remember-me", userDetailsService, new InMemoryTokenRepositoryImpl(), userRepository);
        return rememberMeServices;
    }

    @Bean
    public FilterRegistrationBean<JwtAuthorizationFilter> authorizationFilterRegistrationBean() {
        FilterRegistrationBean<JwtAuthorizationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(jwtAuthorizationFilter);
        registrationBean.addUrlPatterns("/api/**");
        return registrationBean;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = "ROLE_ADMIN > ROLE_STAFF \n ROLE_STAFF > ROLE_USER";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }

    @Bean
    public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
