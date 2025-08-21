package com.book.store.Controller;


import com.book.store.Entity.Admin;
import com.book.store.Mapper.AdminMapper;
import com.book.store.Service.AdminService;
import com.book.store.Service.BookService;

import com.book.store.security.CustomUserDetails;
import com.book.store.server.api.AdminsApi;
import com.book.store.server.dto.AdminApiDto;
import com.book.store.server.dto.BookFieldUpdateApiDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequiredArgsConstructor
public class AdminController implements AdminsApi {

    private final AdminService adminService;
    private final BookService bookService;
    private final AdminMapper adminMapper;

    @Override
    public ResponseEntity<List<AdminApiDto>> adminGet(Integer page, Integer size) {

        List<AdminApiDto> adminDtos = adminService.findAllAdmins(page, size);

        //  CustomerApiDtoApiDto response = new CustomerApiDtoApiDto();


        return new ResponseEntity<>(adminDtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> adminIdDelete(Long id) {
        adminService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AdminApiDto> adminIdGet(Long id) {
       Admin admin = adminService.findAdminById(id);
       AdminApiDto adminApiDto = adminMapper.toDTO(admin);
        return new ResponseEntity<>(adminApiDto,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> adminPost(AdminApiDto adminApiDto) {
        // Convert API DTO to entity
        Admin adminEntity = adminMapper.toEntity(adminApiDto);
        adminEntity.setUsername(adminApiDto.getUsername());
        adminEntity.setEmail(adminApiDto.getEmail());
        // Save the customer entity
        Admin savedAdmin = adminService.createAdmin(adminEntity);

        // Convert saved entity back to API DTO
        AdminApiDto responseDto = adminMapper.toDTO(savedAdmin);

        // Return response with CREATED status
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> logBookFieldUpdate(
            @PathVariable("id") Long entityId,
            @Valid @RequestBody BookFieldUpdateApiDto bookFieldUpdateApiDto
    ) {
        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String field = bookFieldUpdateApiDto.getField();
        Long changer = userDetails.getUser().getId();
        String oldValue = bookFieldUpdateApiDto.getOldValue();
        String newValue = bookFieldUpdateApiDto.getNewValue();

        adminService.updateBookFields(entityId, field, oldValue, newValue, changer);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}