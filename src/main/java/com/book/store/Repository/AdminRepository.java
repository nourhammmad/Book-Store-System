package com.book.store.Repository;

import com.book.store.Entity.Admin;
import com.book.store.Entity.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    List<Admin> findAllAdmins(Pageable pageable);

    void deleteById(Integer id);

//    Admin save(Admin admin);
}
