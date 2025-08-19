package com.book.store.Controller;


import com.book.store.Entity.Admin;
import com.book.store.Mapper.AdminMapper;
import com.book.store.Mapper.CustomerMapper;
import com.book.store.Repository.AdminRepository;
import com.book.store.Service.AdminService;
import com.book.store.Service.BookService;
import com.book.store.Service.CustomerService;

import com.book.store.server.api.AdminsApi;
import com.book.store.server.dto.AdminApiDto;
import com.book.store.server.dto.BookFieldUpdateApiDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
public class AdminController implements AdminsApi {

    private final AdminService adminService;
    private final BookService bookService;
    private final AdminMapper adminMapper;



    public AdminController(AdminService adminService, BookService bookService, AdminMapper adminMapper) {
        this.adminService = adminService;
        this.bookService = bookService;
        this.adminMapper = adminMapper;
    }

    @Override
    public ResponseEntity<List<AdminApiDto>> adminsGet(Integer page, Integer size) {

        List<AdminApiDto> adminDtos = adminService.findAllAdmins(page, size);

        //  CustomerApiDtoApiDto response = new CustomerApiDtoApiDto();


        return new ResponseEntity<>(adminDtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> adminsIdDelete(Integer id) {
        adminService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AdminApiDto> adminsIdGet(Integer id) {
        adminService.findAdminById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> adminsPost(AdminApiDto adminApiDto) {
        // Convert API DTO to entity
        Admin adminEntity = adminMapper.toEntity(adminApiDto);
        adminEntity.setName(adminApiDto.getName());
        adminEntity.setEmail(adminApiDto.getEmail());
        // Save the customer entity
        Admin savedAdmin = adminService.createAdmin(adminEntity);

        // Convert saved entity back to API DTO
        AdminApiDto responseDto = adminMapper.toDTO(savedAdmin);

        // Return response with CREATED status
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> adminsUpdateBookFieldPost(BookFieldUpdateApiDto bookFieldUpdateApiDto) {
        String field = bookFieldUpdateApiDto.getField();
        Integer changer = bookFieldUpdateApiDto.getChangedBy();
        String oldValue = bookFieldUpdateApiDto.getOldValue();
        String newValue = bookFieldUpdateApiDto.getNewValue();
        Integer entityId = bookFieldUpdateApiDto.getEntityId();
//        bookService.updateBook(bookFieldUpdateApiDto);
        adminService.updateBookFields(entityId,field,oldValue,newValue,changer);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
