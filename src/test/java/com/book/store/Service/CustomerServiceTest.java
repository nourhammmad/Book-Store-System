package com.book.store.Service;

import com.book.store.Entity.Customer;
import com.book.store.Mapper.CustomerMapper;
import com.book.store.Repository.CustomerRepository;
import com.book.store.server.dto.CustomerApiDto;
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
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setAddress("123 Main St");
        customer.setBalance(100.0f);

        customerApiDto = new CustomerApiDto();
        customerApiDto.setId(1L);
        customerApiDto.setName("John Doe");
        customerApiDto.setEmail("john.doe@example.com");
        customerApiDto.setAddress("123 Main St");
        customerApiDto.setBalance(100.0f);
    }

    @Test
    void createCustomerSavesAndReturnsCustomer() {
        when(customerRepository.save(customer)).thenReturn(customer);

        Customer result = customerService.createCustomer(customer);

        assertNotNull(result);
        assertEquals(customer.getId(), result.getId());
        assertEquals(customer.getName(), result.getName());
        assertEquals(customer.getEmail(), result.getEmail());
        verify(customerRepository).save(customer);
    }

    @Test
    void createCustomerWithNullCustomerThrowsException() {

        assertThrows(NullPointerException.class, () -> {
            customerService.createCustomer(null);
        });

        verify(customerRepository, never()).save(any());
    }

    @Test
    void deleteCustomerCallsRepositoryDelete() {
        doNothing().when(customerRepository).deleteById(1L);

        customerService.deleteCustomer(1L);

        verify(customerRepository).deleteById(1L);
    }

    @Test
    void deleteCustomerWithNullIdHandledByRepository() {
        doNothing().when(customerRepository).deleteById(null);

        customerService.deleteCustomer(null);

        verify(customerRepository).deleteById(null);
    }

    @Test
    void getAllCustomersReturnsPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> customerPage = new PageImpl<>(List.of(customer));

        when(customerRepository.findAll(pageable)).thenReturn(customerPage);
        when(customerMapper.toDTO(customer)).thenReturn(customerApiDto);

        List<CustomerApiDto> result = customerService.getAllCustomers(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("john.doe@example.com", result.get(0).getEmail());
        verify(customerRepository).findAll(pageable);
        verify(customerMapper).toDTO(customer);
    }

    @Test
    void getAllCustomersReturnsEmptyListWhenNoCustomers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> emptyPage = new PageImpl<>(List.of());

        when(customerRepository.findAll(pageable)).thenReturn(emptyPage);

        List<CustomerApiDto> result = customerService.getAllCustomers(0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(customerRepository).findAll(pageable);
        verify(customerMapper, never()).toDTO(any());
    }

    @Test
    void getAllCustomersHandlesLargePageSize() {
        Pageable pageable = PageRequest.of(0, 1000);
        Page<Customer> customerPage = new PageImpl<>(List.of(customer));

        when(customerRepository.findAll(pageable)).thenReturn(customerPage);
        when(customerMapper.toDTO(customer)).thenReturn(customerApiDto);

        List<CustomerApiDto> result = customerService.getAllCustomers(0, 1000);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(customerRepository).findAll(pageable);
    }

    @Test
    void findCustomerByIdReturnsCustomerWhenExists() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerMapper.toDTO(customer)).thenReturn(customerApiDto);

        CustomerApiDto result = customerService.findCustomerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(customerRepository).findById(1L);
        verify(customerMapper).toDTO(customer);
    }

    @Test
    void findCustomerByIdThrowsExceptionWhenNotFound() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> customerService.findCustomerById(999L)
        );

        assertEquals("Customer not found", exception.getMessage());
        verify(customerRepository).findById(999L);
        verify(customerMapper, never()).toDTO(any());
    }

    @Test
    void findCustomerByIdThrowsExceptionWhenIdIsNull() {
        when(customerRepository.findById(null)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> customerService.findCustomerById(null)
        );

        assertEquals("Customer not found", exception.getMessage());
        verify(customerRepository).findById(null);
    }
}
