package com.book.store.Book.Store.Repository;

import com.book.store.Book.Store.Entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByBook_Title(String title);
    Optional<Order> findById(UUID id);
    // Pagination example
    Page<Order> findAll(Pageable pageable);
}
