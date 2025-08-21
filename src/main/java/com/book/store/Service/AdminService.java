package com.book.store.Service;

import com.book.store.Entity.Admin;
import com.book.store.Mapper.AdminMapper;
import com.book.store.Repository.*;
import com.book.store.server.dto.AdminApiDto;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final BookHistoryRepository bokHistoryRepository;
    private final BookHistoryService bookHistoryService;
    private final AdminMapper adminMapper;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;


    public Admin createAdmin(Admin admin) {

        admin.setEmail(admin.getEmail());
        admin.setUsername(admin.getUsername());
        return adminRepository.save(admin);
    }

    // Delete customer
    @Transactional
    public void deleteById(Long id) {
        if (id == null) {
            throw new ValidationException("id is null");
        }
        adminRepository.deleteById(id);
    }

    public List<AdminApiDto> findAllAdmins(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return adminRepository.findAll(pageable).stream()
                .map(adminMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Admin findAdminById(Long id) {
        return adminRepository.findById(id).orElseThrow(() -> new IllegalArgumentException ("User with ID " + id + " not found"));
    }

    public void updateBookFields(Long entityId, String field, String oldValue, String newValue,Long changedBy){
         bookRepository.findById(entityId)
                .map(book -> {
                    if (Objects.equals(field.toLowerCase(), "title")) book.setTitle(newValue);
                    if(Objects.equals(field.toLowerCase(), "price")) book.setPrice(Float.parseFloat(newValue));
                    if (Objects.equals(field.toLowerCase(), "author")) {
                        book.setAuthor(newValue);
                       }
                    if (Objects.equals(field.toLowerCase(), "quantity")) book.setQuantity(Integer.parseInt(newValue));
                    if (Objects.equals(field.toLowerCase(), "description")) book.setDescription(newValue);

                    return bookRepository.save(book);
                })
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id " + entityId));

        bookHistoryService.logChange( entityId,  field,  oldValue,  newValue, changedBy);
    }


}