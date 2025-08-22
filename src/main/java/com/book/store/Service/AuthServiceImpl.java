package com.book.store.Service;

import com.book.store.Entity.Admin;
import com.book.store.Entity.Customer;
import com.book.store.Entity.User;
import com.book.store.Repository.AdminRepository;
import com.book.store.Repository.CustomerRepository;
import com.book.store.Repository.UserRepository;
import com.sun.security.auth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void registerUser(String username, String password, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(password);

        // Default to CUSTOMER role for regular registration
        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setPassword(encodedPassword);
        customer.setEmail(email);
        customer.setBalance(100.0f);
        userRepository.save(customer);
        customerRepository.save(customer);
    }

    public void createUserWithRole(String username, String password, String email, String role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(password);

        if ("CUSTOMER".equals(role)) {
            Customer customer = new Customer();
            customer.setUsername(username);
            customer.setPassword(encodedPassword);
            customer.setEmail(email);
            customer.setBalance(100.0f);
            userRepository.save(customer);
            customerRepository.save(customer);
        } else if ("ADMIN".equals(role)) {
            Admin admin = new Admin();
            admin.setUsername(username);
            admin.setPassword(encodedPassword);
            admin.setEmail(email);
            userRepository.save(admin);
            adminRepository.save(admin);
        } else {
            throw new IllegalArgumentException("Invalid role specified");
        }
    }

    public User authenticateUser(String identifier, String password) {
        User user = userRepository.findByUsername(identifier)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username/email or password"));

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username/email or password");
        }

        return user;
    }

    public String getCurrentUserUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

}
