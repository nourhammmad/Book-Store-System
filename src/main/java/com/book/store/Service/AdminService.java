package com.book.store.Service;

import com.book.store.Entity.Admin;
import com.book.store.Entity.Customer;
import com.book.store.Repository.AdminRepository;
import com.book.store.Repository.BookHistoryRepository;
import com.book.store.Repository.CustomerRepository;
import com.book.store.server.dto.CustomerApiDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final BookHistoryRepository bokHistoryRepository;
    private final BookHistoryService bookHistoryService;


    public Admin createAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    // Delete customer
    @Transactional
    public void deleteById(Integer id) {
        adminRepository.deleteById(id);
    }

    public List<Admin> findAllAdmins(Pageable pageable) {
        return adminRepository.findAll(pageable).getContent();
    }

    public Admin findAdminById(Integer id) {
        return adminRepository.findById(id).get();
    }

    public void updateBookFields(Integer entityId, String field, String oldValue, String newValue,Integer changedBy){
        bookHistoryService.logChange( entityId,  field,  oldValue,  newValue, changedBy);
    }


}
