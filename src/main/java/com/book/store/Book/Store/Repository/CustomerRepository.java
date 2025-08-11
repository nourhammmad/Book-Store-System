package com.book.store.Book.Store.Repository;

import com.book.store.Book.Store.Entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerRepository {

    Optional<Customer> findById(Long id);

    Optional<Customer> findByEmail(String email);
    Customer save(Customer customer);
    void deleteById(Long id);
    Page<Customer> findAll();
    Page<Customer> findByNameContaining(String name, Pageable pageable);

    @Query("SELECT c FROM Customer c JOIN c.books b WHERE b.title = :title")
    Page<Customer> findCustomersByBookTitle(String title, Pageable pageable);

    @Query("SELECT c FROM Customer c JOIN c.books b WHERE b.id = :bookId")
    Page<Customer> findCustomersByBookId(String bookId, Pageable pageable);




}
