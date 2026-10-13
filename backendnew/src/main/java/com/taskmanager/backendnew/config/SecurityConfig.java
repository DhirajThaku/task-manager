package com.taskmanager.backendnew.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.taskmanager.backendnew.security.CustomUserDetailsService;
import com.taskmanager.backendnew.security.JwtAuthEntryPoint;
import com.taskmanager.backendnew.security.JwtAuthFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity // only needed for the optional admin endpoint
@EnableMethodSecurity
public class SecurityConfig {

    // we need to take the access for the user lookup service
    // service loads the user from the db
    @Autowired
    private CustomUserDetailsService userDetailsService;

    // we need to take the access of token checking filter

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    // handler that returns 401 when the user is not logged in
    @Autowired
    private JwtAuthEntryPoint jwtAuthEntryPoint;

    // tool for hashing and checking passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // inside security filter chain (step 2) the line
    // .cors(Customizer.withDefaults()) tells spring security to use below bean this
    // will done before the logic
    // CORS rules: which frontend may call this API
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // who may calls the API:only our angular app
        config.setAllowedOrigins(List.of("http://localhost:4200"));

        // which http methods are allowed (options is the preflight)
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // which headers angular may send (Authorization carries the jwt)
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        // Browse may cache the preflight answer for 1 hour
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);// apply to every url

        return source;
    }

    // specialist that knows how to verify the username and pasword using two tools
    // above mentioned
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        // providing two tools to verify username and password
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // receptionist that routes the login attempts to the specialist
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // rule book what protected,public and no session, user out custom filter
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                // user the CORS bean above must run before the logic check
                .cors(Customizer.withDefaults())
                .exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthEntryPoint))
                .authorizeHttpRequests(
                        // "/error" is public so real errors are visible
                        auth -> auth.requestMatchers("/api/auth/**", "/error").permitAll()
                                // browse preflight requests carry no token
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}