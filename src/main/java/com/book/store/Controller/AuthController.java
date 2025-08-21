package com.book.store.Controller;

import com.book.store.Service.AuthService;
import com.book.store.Service.JwtService;
import com.book.store.security.CustomUserDetails;
import com.book.store.server.api.AuthApi;
import com.book.store.server.dto.AuthRequestApiDto;
import com.book.store.server.dto.JwtResponseApiDto;
import com.book.store.server.dto.LoginRequestApiDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<JwtResponseApiDto> login(@Valid @RequestBody LoginRequestApiDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            if (authentication.isAuthenticated()) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                String jwtToken = jwtService.generateToken(userDetails);
                JwtResponseApiDto jwtResponse = new JwtResponseApiDto();
                jwtResponse.setToken(jwtToken);
                return ResponseEntity.ok(jwtResponse);
            } else {
                throw new UsernameNotFoundException("Invalid user request!");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).build();
        }
    }

    @Override
    public ResponseEntity<Void> register(@Valid @RequestBody AuthRequestApiDto request) {
        try {
            authService.registerUser(request.getUsername(), request.getPassword(), request.getEmail(), request.getRole());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}