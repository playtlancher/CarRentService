package com.venediktov.carservice.controllers;

import com.venediktov.carservice.dto.UserDto;
import com.venediktov.carservice.model.User;
import com.venediktov.carservice.model.UserToken;
import com.venediktov.carservice.repositories.UserRepository;
import com.venediktov.carservice.repositories.UserTokenRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import com.venediktov.carservice.services.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final AuthService authService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, UserTokenRepository userTokenRepository, AuthService authService, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userTokenRepository = userTokenRepository;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.UserRole.USER);
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        String token = java.util.UUID.randomUUID().toString();
        UserToken userToken = new UserToken(token, user);
        userTokenRepository.save(userToken);
        
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = request.getHeader("X-Auth-Token");
        if (token != null && !token.isEmpty()) {
            authService.logout(token);
        }
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(java.security.Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }
        return userRepository.findByUsername(principal.getName())
                .map(UserDto::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build());
    }
}
