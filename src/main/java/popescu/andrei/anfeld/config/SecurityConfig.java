package popescu.andrei.anfeld.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Allow all requests to the home page and registration page
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/logout").permitAll()
                        .requestMatchers("/styles/*").permitAll()
                        .requestMatchers("/js/*").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e
                        .defaultAuthenticationEntryPointFor(
                                (request, response, authException) -> response.sendError(401, "error"),
                                new AntPathRequestMatcher("/api/**"))
                )
                .logout(logout -> logout.logoutSuccessUrl("/").permitAll())
                .oauth2Login(Customizer.withDefaults());

        return http.build();
    }
}
