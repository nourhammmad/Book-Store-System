package com.book.store.Book.Store.Repository;

import com.book.store.Book.Store.Entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Orders, UUID> {

    List<Orders> findByBook_Title(String title);
    Optional<Orders> findById(UUID id);
    // Pagination example
    Page<Orders> findAll(Pageable pageable);
}
