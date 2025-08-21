package com.book.store.Repository;

import com.book.store.Entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    // void deleteById(Integer id);
    List<User> findAll();
    Page<User> findByUsernameContaining(String username, Pageable pageable);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}