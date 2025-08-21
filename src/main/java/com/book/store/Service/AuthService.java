package com.book.store.Service;

import com.book.store.Entity.User;

public interface AuthService {
    void registerUser(String username, String password, String email, String role);
    User authenticateUser(String identifier, String password);
}
