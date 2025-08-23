package com.book.store.security;

import com.book.store.entity.Admin;
import com.book.store.entity.Customer;
import com.book.store.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;
    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return roles or authorities if you have them
        String role;
        if (user instanceof Admin) {
            role = "ADMIN";
        } else if (user instanceof Customer) {
            role = "CUSTOMER";
        } else {
            role = "UNKNOWN";
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
      }


    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUser() {
        return user;
    }
}