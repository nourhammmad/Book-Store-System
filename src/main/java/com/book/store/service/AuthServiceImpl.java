package com.book.store.service;

import com.book.store.entity.Admin;
import com.book.store.entity.Customer;
import com.book.store.entity.User;
import com.book.store.repository.AdminRepository;
import com.book.store.repository.CustomerRepository;
import com.book.store.repository.UserRepository;
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

    @Override
    public User registerUser(String username, String password, String email, String address) {
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
        customer.setAddress(address);
        userRepository.save(customer);
        customerRepository.save(customer);
        return customer;
    }

    @Override
    public User createUserWithRole(String username, String password, String email, String role) {
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
            return customer;
        } else if ("ADMIN".equals(role)) {
            Admin admin = new Admin();
            admin.setUsername(username);
            admin.setPassword(encodedPassword);
            admin.setEmail(email);
            userRepository.save(admin);
            adminRepository.save(admin);
            return admin;
        } else
            throw new IllegalArgumentException("Invalid role specified");
    }
}