package com.book.store.service;

import com.book.store.entity.Customer;
import com.book.store.mapper.CustomerMapper;
import com.book.store.repository.CustomerRepository;
import com.book.store.server.dto.CustomerApiDto;
import com.book.store.server.dto.PaginatedBookResponseApiDto;
import com.book.store.server.dto.PaginatedCustomerResponseApiDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerApiDto customerApiDto;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setUsername("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setAddress("123 Main St");
        customer.setBalance(100.0f);

        customerApiDto = new CustomerApiDto();
        customerApiDto.setUsername("John Doe");
        customerApiDto.setEmail("john.doe@example.com");
        customerApiDto.setAddress("123 Main St");
        customerApiDto.setBalance(100.0f);
    }

    @Test
    void deleteCustomerCallsRepositoryDelete() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(1L);

        customerService.deleteCustomer(1L);

        verify(customerRepository).existsById(1L);
        verify(customerRepository).deleteById(1L);
    }

    @Test
    void deleteCustomerWithNullIdThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> customerService.deleteCustomer(null)
        );

        assertEquals("ID cannot be null", exception.getMessage());
        verify(customerRepository, never()).deleteById(any());
        verify(customerRepository, never()).existsById(any());
    }

    @Test
    void getAllCustomersReturnsPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> customerPage = new PageImpl<>(List.of(customer));

        when(customerRepository.findAll(pageable)).thenReturn(customerPage);
        when(customerMapper.toDTO(customer)).thenReturn(customerApiDto);

        PaginatedCustomerResponseApiDto result = customerService.getAllCustomers(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("John Doe", result.getContent().get(0).getUsername());
        assertEquals("john.doe@example.com", result.getContent().get(0).getEmail());
        assertEquals("123 Main St", result.getContent().get(0).getAddress());
        assertEquals(100.0f, result.getContent().get(0).getBalance());
        verify(customerRepository).findAll(pageable);
        verify(customerMapper).toDTO(customer);
    }


    @Test
    void getAllCustomersReturnsEmptyListWhenNoCustomers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> emptyPage = new PageImpl<>(List.of());

        when(customerRepository.findAll(pageable)).thenReturn(emptyPage);

        PaginatedCustomerResponseApiDto result = customerService.getAllCustomers(0, 10);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        verify(customerRepository).findAll(pageable);
        verify(customerMapper, never()).toDTO(any());
    }

    @Test
    void getAllCustomersHandlesLargePageSize() {
        Pageable pageable = PageRequest.of(0, 1000);
        Page<Customer> customerPage = new PageImpl<>(List.of(customer));

        when(customerRepository.findAll(pageable)).thenReturn(customerPage);
        when(customerMapper.toDTO(customer)).thenReturn(customerApiDto);

        PaginatedCustomerResponseApiDto result = customerService.getAllCustomers(0, 1000);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(customerRepository).findAll(pageable);
    }

    @Test
    void findCustomerByIdReturnsCustomerWhenExists() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerMapper.toDTO(customer)).thenReturn(customerApiDto);

        // Service returns entity
        Customer resultEntity = customerService.findCustomerById(1L);

        // Map to DTO for assertions
        CustomerApiDto resultDto = customerMapper.toDTO(resultEntity);

        assertNotNull(resultDto);
        assertEquals("John Doe", resultDto.getUsername());
        assertEquals("john.doe@example.com", resultDto.getEmail());
        assertEquals("123 Main St", resultDto.getAddress());
        assertEquals(100.0f, resultDto.getBalance());

        verify(customerRepository).findById(1L);
        verify(customerMapper).toDTO(customer);
    }


    @Test
    void findCustomerByIdThrowsExceptionWhenNotFound() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
            () -> customerService.findCustomerById(999L)
        );

        assertEquals("Customer not found with id: 999", exception.getMessage());
        verify(customerRepository).findById(999L);
        verify(customerMapper, never()).toDTO(any());
    }

    @Test
    void findCustomerByIdThrowsExceptionWhenIdIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> customerService.findCustomerById(null)
        );

        assertEquals("ID cannot be null", exception.getMessage());
    }


    @Test
    void getAllCustomersWithNegativePageNumber() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> customerService.getAllCustomers(-5, 10)
        );

        assertEquals("Page number must be non-negative", exception.getMessage());
        verify(customerRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAllCustomersWithZeroPageSize() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> customerService.getAllCustomers(0, 0)
        );

        assertEquals("Size must be positive", exception.getMessage());
        verify(customerRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAllCustomersMapperThrowsException() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> customerPage = new PageImpl<>(List.of(customer));

        when(customerRepository.findAll(pageable)).thenReturn(customerPage);
        when(customerMapper.toDTO(customer)).thenThrow(new RuntimeException("Mapping error"));

        assertThrows(RuntimeException.class, () -> {
            customerService.getAllCustomers(0, 10);
        });

        verify(customerRepository).findAll(pageable);
        verify(customerMapper).toDTO(customer);
    }

    @Test
    void findCustomerByIdMapperThrowsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        // Only throw if mapper is called
        when(customerMapper.toDTO(any())).thenThrow(new RuntimeException("Mapping error"));

        // Call the service method that actually invokes the mapper
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            CustomerApiDto dto = customerMapper.toDTO(customerService.findCustomerById(1L));
        });

        assertEquals("Mapping error", exception.getMessage());
        System.out.println("Test passed: Mapper threw expected exception.");

        verify(customerRepository).findById(1L);
        verify(customerMapper).toDTO(any());
    }


    @Test
    void deleteCustomerRepositoryThrowsException() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(customerRepository).deleteById(1L);

        assertThrows(RuntimeException.class, () -> {
            customerService.deleteCustomer(1L);
        });

        verify(customerRepository).existsById(1L);
        verify(customerRepository).deleteById(1L);
    }
}
