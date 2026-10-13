package com.taskmanager.backendnew.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanager.backendnew.dto.AuthResponse;
import com.taskmanager.backendnew.dto.ErrorResponse;
import com.taskmanager.backendnew.dto.LoginRequest;
import com.taskmanager.backendnew.dto.RegisterRequest;
import com.taskmanager.backendnew.entity.User;
import com.taskmanager.backendnew.security.JwtService;
import com.taskmanager.backendnew.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        User user = userService.registerUser(request); // throws -> handler returns 409
        String token = jwtService.generateToken(user.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, user.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {

            Authentication auth = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            String username = auth.getName();
            String token = jwtService.generateToken(username);
            return ResponseEntity.ok(new AuthResponse(token, request.getUsername()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse.of(401, "Unauthorized", "Invalid username or password"));
        }
    }
}
