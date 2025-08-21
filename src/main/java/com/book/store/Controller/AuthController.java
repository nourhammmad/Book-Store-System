package com.book.store.Controller;

import com.book.store.Service.AuthService;
import com.book.store.Service.JwtService;
import com.book.store.server.api.AuthApi;
import com.book.store.server.dto.JwtResponseApiDto;
import com.book.store.server.dto.LoginRequestApiDto;
import com.book.store.server.dto.RegisterRequestApiDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<JwtResponseApiDto> login(LoginRequestApiDto request) {
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
    }

    @Override
    public ResponseEntity<Void> register(RegisterRequestApiDto request) {
        authService.registerUser(request.getUsername(), request.getPassword(), request.getEmail(), request.getRole());
        return ResponseEntity.ok().build();
    }

}