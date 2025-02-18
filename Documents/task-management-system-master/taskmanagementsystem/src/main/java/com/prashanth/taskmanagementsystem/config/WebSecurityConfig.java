package com.prashanth.taskmanagementsystem.config;

import com.prashanth.taskmanagementsystem.security.JwtTokenFilter;
import com.prashanth.taskmanagementsystem.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())  // Disable CSRF for API security
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Stateless session

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/static/**", "/resources/**", "/**.html", "/**.css", "/**.js").permitAll()   // Allow access to frontend files
                        .requestMatchers("/api/v1/user/signup", "/api/v1/user/signin").permitAll()  // Allow auth endpoints

                        .requestMatchers("/api/v1/user/signup", "/api/v1/user/signin").permitAll()  // Allow authentication APIs
                        .requestMatchers("/api/v1/task/update/**").hasAnyRole("ADMIN", "USER")  // Allow only Admin & User
                        .requestMatchers("/api/v1/task/all").hasRole("ADMIN")  // Only Admin can view all tasks
                        .requestMatchers("/api/v1/task/assign/**").hasRole("ADMIN")  // Only Admin can assign tasks
                        .requestMatchers("/api/v1/task/filter/**").hasRole("ADMIN")  // Admin can filter tasks
                        .requestMatchers("/api/v1/comment/**").hasAnyRole("ADMIN", "USER")  // Comments allowed for Admin & User
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()  // Allow Swagger UI
                        .anyRequest().authenticated()  // Secure all other requests
                )

                // Add JWT Authentication Filter
                .addFilterBefore(new JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Password encoder bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Authentication manager bean
     */
    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
