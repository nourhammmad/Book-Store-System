package com.book.store.Repository;

import com.book.store.Entity.Customer;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer>{


       // Optional<Customer> findByEmail(String email);
       // void deleteById(Integer id);


      //  Page<Customer> findAll(Pageable pageable);
        Page<Customer> findByNameContaining(String name, Pageable pageable);



}
