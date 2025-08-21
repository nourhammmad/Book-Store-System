package com.book.store.Controller;

import com.book.store.Entity.Admin;
import com.book.store.Mapper.AdminMapper;
import com.book.store.Service.AdminService;
import com.book.store.server.api.CustomersApi;

import com.book.store.server.dto.AdminApiDto;
import com.book.store.server.dto.BookFieldUpdateApiDto;
import com.book.store.server.dto.CustomerApiDto;

import com.book.store.Service.CustomerService;
import com.book.store.Entity.Customer;
import com.book.store.Mapper.CustomerMapper;
import com.book.store.server.dto.CustomerApiDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CustomerController implements CustomersApi {


    private final CustomerService customerService;
    private final CustomerMapper customerMapper;



    public CustomerController(CustomerService customerService, CustomerMapper customerMapper) {
        this.customerService = customerService;
        this.customerMapper = customerMapper;

    }

//    @Override
//    public ResponseEntity<CustomersApiDto> findAllCustomers(Integer page, Integer size) {
//
//    }

//
//    @Override
//    public ResponseEntity<CustomerApiDto> findCustomerById(Integer id) {
//        return null;
//    }





    @Override
   public ResponseEntity<List<CustomerApiDto>> customersGet(Integer page, Integer size) {
//        List<CustomerApiDto> customerDtos = customerService.getAllCustomers(page, size);
//        CustomerApiDtoApiDto response = (CustomerApiDtoApiDto) customerDtos;
//
//        return new ResponseEntity<>( response,HttpStatus.OK);

        return null;
    }

//    @Override
//    public ResponseEntity<List<CustomerApiDto>> customersGet() {
//        List<CustomerApiDto> customerDtos = customerService.getAllCustomers(page, size);
//
//        CustomersApiDto response = new CustomersApiDto();
//        response.setCustomers(customerDtos);
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

    @Override
    public ResponseEntity<Void> customersIdDelete(Long id) {
         customerService.deleteCustomer(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override

    public ResponseEntity<CustomerApiDto> customersIdGet(Long id) {
        customerService.findCustomerById(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> customersPost(CustomerApiDto customerApiDto) {
        // Convert API DTO to entity
        Customer customerEntity = customerMapper.toEntity(customerApiDto);
        customerEntity.setUsername(customerApiDto.getUsername());
        customerEntity.setEmail(customerApiDto.getEmail());
        // Save the customer entity
        Customer savedCustomer = customerService.createCustomer(customerEntity);

        // Convert saved entity back to API DTO
        CustomerApiDto responseDto = customerMapper.toDTO(savedCustomer);

        // Return response with CREATED status
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }
}