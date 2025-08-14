package com.book.store.Repository;

import com.book.store.Entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findById(Long id);

    Optional<Customer> findByEmail(String email);
    Customer save(Customer customer);
    void deleteById(UUID id);
    void deleteById(Long id);
    List<Customer> findAll();
    Page<Customer> findByNameContaining(String name, Pageable pageable);
}
