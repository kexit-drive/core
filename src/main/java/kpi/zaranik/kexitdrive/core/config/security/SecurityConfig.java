package kpi.zaranik.kexitdrive.core.config.security;

import static org.springframework.security.config.Customizer.withDefaults;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] PERMIT_ALL = {
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/v3/api-docs/**"
    };

    @Bean
    SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        AuthenticationSuccessHandler customAuthenticationSuccessHandler,
        AuthenticationFailureHandler customAuthenticationFailureHandler,
        LogoutSuccessHandler customLogoutSuccessHandler
    ) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .logout(l -> l.logoutSuccessHandler(customLogoutSuccessHandler))
            .oauth2Login(login -> login
                .successHandler(customAuthenticationSuccessHandler)
                .failureHandler(customAuthenticationFailureHandler)
            )
            .oauth2Client(withDefaults())
            .authorizeHttpRequests(requests ->
                requests
                    .requestMatchers(PERMIT_ALL).permitAll()
                    .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            response.sendRedirect("/");
        };
    }

    @Bean
    LogoutSuccessHandler customLogoutSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            response.sendRedirect("/");
        };
    }

    @Bean
    AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return (request, response, exception) -> {
            response.sendRedirect("/");
        };
    }

}
