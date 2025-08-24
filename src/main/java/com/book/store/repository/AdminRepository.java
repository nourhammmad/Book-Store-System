package com.book.store.repository;

import com.book.store.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Page<Admin> findAll(Pageable pageable);


    void deleteById(Integer id);

//    Admin save(Admin admin);
}
