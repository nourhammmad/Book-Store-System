package com.book.store.Controller;
import com.book.store.server.api.CustomersApi;
import com.book.store.server.dto.CustomerApiDto;
import com.book.store.server.dto.CustomerReferenceApiDto;
import com.book.store.Service.CustomerService;
import com.book.store.Entity.Customer;
import com.book.store.Mapper.CustomerMapper;
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
    public ResponseEntity<List<CustomerApiDto>> customerGet(Integer page, Integer size) {
        List<CustomerApiDto> referenceDtos = customerService.getAllCustomers(page, size);
        return ResponseEntity.ok(referenceDtos);
    }
    @Override
    public ResponseEntity<Void> customerIdDelete(Long id) {
         customerService.deleteCustomer(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<CustomerApiDto> customerIdGet(Long id) {
        Customer customer = customerService.findCustomerById(id); // return entity
        CustomerApiDto dto = customerMapper.toDTO(customer);           // map to API DTO
        return ResponseEntity.ok(dto);
    }



//    @Override
//    public ResponseEntity<Void> customerPost(CustomerApiDto customerApiDto) {
//        // Convert API DTO to entity
//        Customer customerEntity = customerMapper.toEntity(customerApiDto);
//        customerEntity.setUsername(customerApiDto.getUsername());
//        customerEntity.setEmail(customerApiDto.getEmail());
//        // Save the customer entity
//        Customer savedCustomer = customerService.createCustomer(customerEntity);
//
//        // Convert saved entity back to API DTO
//        CustomerApiDto responseDto = customerMapper.toDTO(savedCustomer);
//
//        // Return response with CREATED status
//        return new ResponseEntity<Void>(HttpStatus.CREATED);
//    }
}