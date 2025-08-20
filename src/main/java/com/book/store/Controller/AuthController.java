package com.book.store.Controller;

import com.book.store.Service.AuthService;
import com.book.store.Service.JwtService;
import com.book.store.request.AuthRequest;
import com.book.store.request.LoginRequest;
import com.book.store.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            if (authentication.isAuthenticated()) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                return ResponseEntity.ok(jwtService.generateToken(userDetails));
            } else {
                throw new UsernameNotFoundException("Invalid user request!");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest request) {
        try {
            authService.registerUser(request.getUsername(), request.getPassword(), request.getEmail());
            return ResponseEntity.ok().body("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/whoami")
    public ResponseEntity<?> whoami(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok().body("Hello, " + userDetails.getUsername());
    }
}
