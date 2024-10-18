package com.example.demo.config;

import com.example.demo.service.UserDetailsServiceImpl;

import jakarta.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // Inject your custom UserDetailsService

    // Bean for password encoding (using BCrypt)
    // @Bean
    // static PasswordEncoder passwordEncoder() {
    // return NoOpPasswordEncoder.getInstance(); // Use NoOpPasswordEncoder for
    // plain text passwords
    // }

    @Bean
    static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Use BCrypt for hashing
    }

    // Security configuration using SecurityFilterChain
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/login", "/styles.css", "/js/").permitAll() // Ensure /register and
                                                                                           // /login are accessible
                        .requestMatchers("/home").authenticated() // Ensure /home is accessible only to authenticated
                                                                  // users
                        .requestMatchers("/edit/", "/register", "/update/", "/delete/").permitAll()
                        .anyRequest().authenticated() // Secure all other requests
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/perform_logout")
                        .logoutSuccessUrl("/register")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .addLogoutHandler((request, response, authentication) -> {
                            Cookie cookie = new Cookie("JSESSIONID", null);
                            cookie.setPath("/");
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                        }))
                .sessionManagement((session) -> session
                        .invalidSessionUrl("/login")
                        .maximumSessions(1)
                        .expiredUrl("/login?expired=true"));

        return http.build(); // Don't forget to return the security filter chain
    }

    // Bean for AuthenticationManager
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Custom Authentication Provider
    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Set the custom UserDetailsService
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
