package com.book.store.controller;

import com.book.store.service.AuthService;
import com.book.store.service.JwtService;
import com.book.store.server.api.AuthApi;
import com.book.store.server.dto.JwtResponseApiDto;
import com.book.store.server.dto.LoginRequestApiDto;
import com.book.store.server.dto.RegisterRequestApiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<JwtResponseApiDto> login(LoginRequestApiDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        if (authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwtToken = jwtService.generateToken(userDetails);
            String authority = userDetails.getAuthorities().iterator().next().getAuthority(); // e.g., "ROLE_ADMIN"
            JwtResponseApiDto.RoleEnum roleEnum = JwtResponseApiDto.RoleEnum.valueOf(authority.replace("ROLE_", ""));
            JwtResponseApiDto jwtResponse = new JwtResponseApiDto(jwtToken, userDetails.getUsername(), roleEnum);
            return ResponseEntity.ok(jwtResponse);
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    @Override
    public ResponseEntity<Void> register(RegisterRequestApiDto request) {
        authService.registerUser(request.getUsername(), request.getPassword(), request.getEmail());
        return ResponseEntity.status(201).build();
    }
}