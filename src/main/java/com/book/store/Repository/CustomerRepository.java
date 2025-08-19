package com.book.store.Repository;

import com.book.store.Entity.Customer;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Customer> findById(Long id);

    Optional<Customer> findByEmail(String email);
    Customer save(Customer customer);
    void deleteById(Long id);
    List<Customer> findAll();
    Page<Customer> findByNameContaining(String name, Pageable pageable);
}
