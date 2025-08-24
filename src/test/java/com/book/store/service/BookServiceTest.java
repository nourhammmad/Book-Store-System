package com.book.store.service;

import com.book.store.entity.Book;
import com.book.store.mapper.BookMapper;
import com.book.store.repository.BookRepository;
import com.book.store.server.dto.BookApiDto;
import com.book.store.server.dto.PaginatedBookResponseApiDto;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private BookApiDto bookApiDto;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setIsbn("9781234567892");
        book.setPrice(29.99f);
        book.setQuantity(10);
        book.setDescription("Test Description");

        bookApiDto = new BookApiDto(1L, "9781234567892", "Test Book", "Test Author", 29.99f);
    }

    @Test
    void getAllBooksReturnsPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(book));
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookApiDto);

        PaginatedBookResponseApiDto result = bookService.getAllBooks(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Author", result.getContent().get(0).getTitle());
        verify(bookRepository).findAll(pageable);
        verify(bookMapper).toDto(book);
    }

    @Test
    void getAllBooksReturnsEmptyListWhenNoBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> emptyPage = new PageImpl<>(List.of());

        when(bookRepository.findAll(pageable)).thenReturn(emptyPage);

        PaginatedBookResponseApiDto result = bookService.getAllBooks(0, 10);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        verify(bookRepository).findAll(pageable);
    }

    @Test
    void getBookByIdReturnsBookWhenExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(1L);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Book", result.getTitle());
        verify(bookRepository).findById(1L);
    }

    @Test
    void getBookByIdThrowsExceptionWhenNotFound() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.getBookById(999L)
        );

        assertEquals("Book not found with id: 999", exception.getMessage());
        verify(bookRepository).findById(999L);
    }

    @Test
    void createBookSavesAndReturnsBook() {
        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookService.createBook(book);

        assertNotNull(result);
        assertEquals(book.getId(), result.getId());
        assertEquals(book.getTitle(), result.getTitle());
        verify(bookRepository).save(book);
    }
    @Test
    void deleteBookCallsRepositoryDelete() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        verify(bookRepository).existsById(1L);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void getDescriptionByIdReturnsDescription() {
        String expectedDescription = "Test Description";
        when(bookRepository.getDescriptionById(1L)).thenReturn(expectedDescription);

        String result = bookService.getDescriptionById(1L);

        assertEquals(expectedDescription, result);
        verify(bookRepository).getDescriptionById(1L);
    }

    @Test
    void getDescriptionByIdReturnsNullWhenNotFound() {
        when(bookRepository.getDescriptionById(999L)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.getDescriptionById(999L)
        );

        assertEquals("Book not found with id: 999", exception.getMessage());
        verify(bookRepository).getDescriptionById(999L);
    }

    @Test
    void getAllBooksHandlesNegativePageNumber() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.getAllBooks(-1, 10)
        );

        assertEquals("Page number must be non-negative", exception.getMessage());
        verify(bookRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAllBooksHandlesZeroPageSize() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.getAllBooks(0, 0)
        );

        assertEquals("Size must be positive", exception.getMessage());
        verify(bookRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void createBookWithNullFields() {
        Book bookWithNulls = new Book();
        bookWithNulls.setTitle("Only Title");

        when(bookRepository.save(bookWithNulls)).thenReturn(bookWithNulls);

        Book result = bookService.createBook(bookWithNulls);

        assertNotNull(result);
        assertEquals("Only Title", result.getTitle());
        verify(bookRepository).save(bookWithNulls);
    }

    @Test
    void deleteBookWithNonExistentId() {
        when(bookRepository.existsById(999L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.deleteBook(999L)
        );

        assertEquals("Book not found with id: 999", exception.getMessage());
        verify(bookRepository).existsById(999L);
        verify(bookRepository, never()).deleteById(999L);
    }

    @Test
    void getDescriptionByIdWithEmptyString() {
        when(bookRepository.getDescriptionById(1L)).thenReturn("");

        String result = bookService.getDescriptionById(1L);

        assertEquals("", result);
        verify(bookRepository).getDescriptionById(1L);
    }
}
