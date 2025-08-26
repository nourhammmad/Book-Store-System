package com.book.store.controller;

import com.book.store.entity.Admin;
import com.book.store.entity.Book;
import com.book.store.entity.User;
import com.book.store.mapper.AdminMapper;
import com.book.store.mapper.BookMapper;
import com.book.store.server.dto.*;
import com.book.store.service.AdminService;
import com.book.store.service.AuthService;
import com.book.store.security.CustomUserDetails;
import com.book.store.server.api.AdminsApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController implements AdminsApi {

    private final AdminService adminService;
    private final AuthService authService;
    private final AdminMapper adminMapper;
    private final BookMapper bookMapper;

    @Override
    public ResponseEntity<PaginatedAdminResponseApiDto> adminGet(Integer page, Integer size) {
        PaginatedAdminResponseApiDto adminDtos = adminService.findAllAdmins(page, size);
        return ResponseEntity.ok(adminDtos);
    }

    @Override
    public ResponseEntity<Void> adminIdDelete(Long id) {
        adminService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<AdminApiDto> adminIdGet(Long id) {
        Admin admin = adminService.findAdminById(id);
        AdminApiDto adminApiDto = adminMapper.toDTO(admin);
        return ResponseEntity.ok(adminApiDto);
    }

    @Override
    public ResponseEntity<UserCreatedResponseApiDto> createUserWithRole(CreateUserRequestApiDto createUserRequestApiDto) {
        User user = authService.createUserWithRole(
                createUserRequestApiDto.getUsername(),
                createUserRequestApiDto.getPassword(),
                createUserRequestApiDto.getEmail(),
                createUserRequestApiDto.getRole().getValue()
        );

        UserCreatedResponseApiDto response = new UserCreatedResponseApiDto(user.getId(), user.getUsername(), user.getEmail()
                ,UserCreatedResponseApiDto.RoleEnum.fromValue(createUserRequestApiDto.getRole().getValue()), "User created successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<BookApiDto> logBookFieldUpdate(Long id, BookFieldUpdateApiDto bookFieldUpdateApiDto) {
        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Book updatedBook = adminService.updateBookFields(
                id,
                bookFieldUpdateApiDto.getField().getValue(),
                bookFieldUpdateApiDto.getOldValue(),
                bookFieldUpdateApiDto.getNewValue(),
                userDetails.getUser().getId()
        );
        BookApiDto bookApiDto = bookMapper.toDto(updatedBook);
        return ResponseEntity.ok(bookApiDto);
    }
}