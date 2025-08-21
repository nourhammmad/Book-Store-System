package com.book.store.Service;

import com.book.store.Entity.User;
import com.book.store.server.dto.RegisterRequestApiDto;

public interface AuthService {
    void registerUser(String username, String password, String email, RegisterRequestApiDto.RoleEnum role);
    User authenticateUser(String identifier, String password);
}
