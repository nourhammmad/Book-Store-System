package com.book.store.service;

import com.book.store.entity.Admin;
import com.book.store.entity.Book;
import com.book.store.mapper.AdminMapper;
import com.book.store.repository.*;
import com.book.store.server.dto.AdminApiDto;
import com.book.store.server.dto.PaginatedAdminResponseApiDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final BookHistoryService bookHistoryService;
    private final AdminMapper adminMapper;
    private final BookRepository bookRepository;

    @Transactional
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        if (!adminRepository.existsById(id)) {
            throw new EntityNotFoundException("Admin with ID " + id + " not found");
        }
        adminRepository.deleteById(id);
    }

    public PaginatedAdminResponseApiDto findAllAdmins(Integer page, Integer size) {
        int pageNum = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 10;
        Pageable pageable = PageRequest.of(pageNum, pageSize);

        var adminPage = adminRepository.findAll(pageable);
        List<AdminApiDto> content = adminPage.stream()
                .map(adminMapper::toDTO)
                .toList();

        return new PaginatedAdminResponseApiDto(
                content,
                adminPage.getNumber(),
                adminPage.getSize(),
                adminPage.getTotalElements(),
                adminPage.getTotalPages()
        )
                .first(adminPage.isFirst())
                .last(adminPage.isLast());
    }

    public Admin findAdminById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return adminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Admin with ID " + id + " not found"));
    }

    public Book updateBookFields(Long entityId, String field, String oldValue, String newValue, Long changedBy) {
        if (entityId == null) {
            throw new IllegalArgumentException("entityId is null");
        }
        if (field == null || field.isBlank()) {
            throw new IllegalArgumentException("field is null or empty");
        }
        if (newValue == null) {
            throw new IllegalArgumentException("newValue is null");
        }
        if (changedBy == null) {
            throw new IllegalArgumentException("changedBy is null");
        }

        String normalizedField = field.trim().toLowerCase();
        switch (normalizedField) {
            case "title":
            case "author":
            case "isbn":
            case "description":
                break;
            case "price":
                try {
                    Float.parseFloat(newValue);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid value for price: " + newValue);
                }
                break;
            case "quantity":
                try {
                    Integer.parseInt(newValue);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid value for quantity: " + newValue);
                }
                break;
            default:
                throw new IllegalArgumentException("Field '" + field + "' cannot be updated");
        }

        Book updatedBook = bookRepository.findById(entityId)
                .map(book -> {
                    switch (normalizedField) {
                        case "title" -> book.setTitle(newValue);
                        case "isbn" -> book.setIsbn(newValue);
                        case "price" -> book.setPrice(Float.parseFloat(newValue));
                        case "author" -> book.setAuthor(newValue);
                        case "quantity" -> book.setQuantity(Integer.parseInt(newValue));
                        case "description" -> book.setDescription(newValue);
                    }
                    return bookRepository.save(book);
                })
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id " + entityId));

        bookHistoryService.logChange(entityId, field, oldValue, newValue, changedBy);

        return updatedBook;
    }
}