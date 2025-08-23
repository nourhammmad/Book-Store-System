package com.book.store.service;

import com.book.store.entity.User;

public interface AuthService {
    void registerUser(String username, String password, String email);
    User createUserWithRole(String username, String password, String email, String role);
    User authenticateUser(String identifier, String password);
    String getCurrentUserUsername();
}
