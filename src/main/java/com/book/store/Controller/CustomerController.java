package com.book.store.Controller;

import com.book.store.server.api.CustomersApi;
import com.book.store.server.dto.CustomerApiDto;
import com.book.store.server.dto.CustomersApiDto;
import com.book.store.Service.CustomerService;
import com.book.store.Entity.Customer;
import com.book.store.Mapper.CustomerMapper; // you will create this like OrderMapper
import com.book.store.server.api.CustomersApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CustomerController implements CustomersApi {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    public CustomerController(CustomerService customerService, CustomerMapper customerMapper) {
        this.customerService = customerService;
        this.customerMapper = customerMapper;
    }

    @Override
    public ResponseEntity<CustomersApiDto> findAllCustomers(Integer page, Integer size) {
        List<CustomerApiDto> customerDtos = customerService.getAllCustomers(page, size);

        CustomersApiDto response = new CustomersApiDto();
        response.setCustomers(customerDtos);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<CustomerApiDto> findCustomerById(Integer id) {
        return null;
    }


    @Override
    public ResponseEntity<CustomerApiDto> createCustomer(CustomerApiDto customerApiDto) {
        // Convert API DTO to entity
        Customer customerEntity = customerMapper.toEntity(customerApiDto);

        // Save the customer entity
        Customer savedCustomer = customerService.createCustomer(customerEntity);

        // Convert saved entity back to API DTO
        CustomerApiDto responseDto = customerMapper.toDTO(savedCustomer);

        // Return response with CREATED status
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteCustomerById(Integer id) {
        return null;
    }


}
