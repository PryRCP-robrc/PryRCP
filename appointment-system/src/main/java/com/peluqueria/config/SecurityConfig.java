package com.peluqueria.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Seguridad para ADMIN
    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurity(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/admin/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/login", "/admin/logout", "/css/**", "/js/**", "/images/**").permitAll()
                .anyRequest().hasRole("ADMIN")
            )
            .formLogin(form -> form
                .loginPage("/admin/login")
                .loginProcessingUrl("/admin/login")
                .defaultSuccessUrl("/admin/dashboard", true)
                .failureUrl("/admin/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/login?logout=true")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    // Seguridad para CLIENTE
    @Bean
    @Order(2)
    public SecurityFilterChain clienteSecurity(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/cliente-login", "/cliente-registro", "/css/**", "/js/**", "/images/**",
                    "/contacto", "/servicios", "/citas/**", "/api/**", "/panel"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/cliente-login")
                .loginProcessingUrl("/cliente-login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/cliente-login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
