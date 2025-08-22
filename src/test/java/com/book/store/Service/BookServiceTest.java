package com.book.store.Service;

import com.book.store.Entity.Admin;
import com.book.store.Entity.Book;
import com.book.store.Mapper.BookMapper;
import com.book.store.Repository.BookRepository;
import com.book.store.server.dto.BookApiDto;
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
        book.setPrice(29.99f);
        book.setQuantity(10);
        book.setDescription("Test Description");

        bookApiDto = new BookApiDto();
        bookApiDto.setId(1L);
        bookApiDto.setTitle("Test Book");
        bookApiDto.setPrice(29.99f);
    }

    @Test
    void getAllBooksReturnsPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookApiDto);

        List<BookApiDto> result = bookService.getAllBooks(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
        verify(bookRepository).findAll(pageable);
        verify(bookMapper).toDto(book);
    }

    @Test
    void getAllBooksReturnsEmptyListWhenNoBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> emptyPage = new PageImpl<>(List.of());

        when(bookRepository.findAll(pageable)).thenReturn(emptyPage);

        List<BookApiDto> result = bookService.getAllBooks(0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
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

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bookService.getBookById(999L)
        );

        assertEquals("Order not found", exception.getMessage());
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
//
//    @Test
//    void updateBookUpdatesExistingBookSuccessfully() {
//        BookApiDto updateDto = new BookApiDto();
//        updateDto.setTitle("Updated Title");
//        updateDto.setPrice(39.99f);
//
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
//        when(bookRepository.save(any(Book.class))).thenReturn(book);
//
//        Book result = bookService.updateBook(updateDto, 1L);
//
//        assertNotNull(result);
//        assertEquals("Updated Title", book.getTitle());
//        assertEquals(39.99f, book.getPrice());
//        verify(bookRepository).findById(1L);
//        verify(bookRepository).save(book);
//    }

//    @Test
//    void updateBookOnlyUpdatesNonNullFields() {
//        BookApiDto updateDto = new BookApiDto();
//        updateDto.setTitle("Updated Title");
//        updateDto.setPrice(0.0f);
//
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
//        when(bookRepository.save(any(Book.class))).thenReturn(book);
//
//        Book result = bookService.updateBook(updateDto, 1L);
//
//        assertNotNull(result);
//        assertEquals("Updated Title", book.getTitle());
//        assertEquals(29.99f, book.getPrice()); // Original price preserved
//        verify(bookRepository).findById(1L);
//        verify(bookRepository).save(book);
//    }

//    @Test
//    void updateBookThrowsExceptionWhenNotFound() {
//        BookApiDto updateDto = new BookApiDto();
//        updateDto.setTitle("Updated Title");
//
//        when(bookRepository.findById(999L)).thenReturn(Optional.empty());
//
//        RuntimeException exception = assertThrows(
//            RuntimeException.class,
//            () -> bookService.updateBook(updateDto, 999L)
//        );
//
//        assertEquals("Book not found with id 999", exception.getMessage());
//        verify(bookRepository).findById(999L);
//        verify(bookRepository, never()).save(any());
//    }

    @Test
    void deleteBookCallsRepositoryDelete() {
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        verify(bookRepository).deleteById(1L);
    }

    @Test
    void getDescriptionByIdReturnsDescription() {
        String expectedDescription = "Test Description";
        when(bookRepository.getDescriptionById(1L)).thenReturn(expectedDescription);

        String result = bookService.GetDescriptionById(1L);

        assertEquals(expectedDescription, result);
        verify(bookRepository).getDescriptionById(1L);
    }

    @Test
    void getDescriptionByIdReturnsNullWhenNotFound() {
        when(bookRepository.getDescriptionById(999L)).thenReturn(null);

        String result = bookService.GetDescriptionById(999L);

        assertNull(result);
        verify(bookRepository).getDescriptionById(999L);
    }

    @Test
    void getAllBooksHandlesNegativePageNumber() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bookService.getAllBooks(-1, 10)
        );

        assertEquals("Page number must be non-negative and size must be positive", exception.getMessage());
        verify(bookRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAllBooksHandlesZeroPageSize() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bookService.getAllBooks(0, 0)
        );

        assertEquals("Page number must be non-negative and size must be positive", exception.getMessage());
        verify(bookRepository, never()).findAll(any(Pageable.class));
    }

//    @Test
//    void updateBookWithAllNullFields() {
//        BookApiDto updateDto = new BookApiDto();
//        updateDto.setPrice(null);
//
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
//        when(bookRepository.save(any(Book.class))).thenReturn(book);
//
//        Book result = bookService.updateBook(updateDto, 1L);
//
//        assertNotNull(result);
//        assertEquals("Test Book", book.getTitle());
//        assertEquals(29.99f, book.getPrice());
//        verify(bookRepository).save(book);
//    }

//    @Test
//    void updateBookWithZeroPrice() {
//        BookApiDto updateDto = new BookApiDto();
//        updateDto.setTitle("Updated Title");
//        updateDto.setPrice(0.0f);
//
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
//        when(bookRepository.save(any(Book.class))).thenReturn(book);
//
//        Book result = bookService.updateBook(updateDto, 1L);
//
//        assertEquals("Updated Title", book.getTitle());
//        assertEquals(29.99f, book.getPrice());
//    }

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
        doNothing().when(bookRepository).deleteById(999L);

        assertDoesNotThrow(() -> bookService.deleteBook(999L));

        verify(bookRepository).deleteById(999L);
    }

    @Test
    void getDescriptionByIdWithEmptyString() {
        when(bookRepository.getDescriptionById(1L)).thenReturn("");

        String result = bookService.GetDescriptionById(1L);

        assertEquals("", result);
        verify(bookRepository).getDescriptionById(1L);
    }
}
