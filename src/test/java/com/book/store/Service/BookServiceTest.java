package com.book.store.Service;

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
        book.setId(1);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPrice(29.99f);
        book.setQuantity(10);
        book.setDescription("Test Description");

        bookApiDto = new BookApiDto();
        bookApiDto.setId(1);
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
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Book", result.getTitle());
        verify(bookRepository).findById(1);
    }

    @Test
    void getBookByIdThrowsExceptionWhenNotFound() {
        when(bookRepository.findById(999)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bookService.getBookById(999)
        );

        assertEquals("Order not found", exception.getMessage());
        verify(bookRepository).findById(999);
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
    void updateBookUpdatesExistingBookSuccessfully() {
        BookApiDto updateDto = new BookApiDto();
        updateDto.setTitle("Updated Title");
        updateDto.setPrice(39.99f);

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book result = bookService.updateBook(updateDto, 1);

        assertNotNull(result);
        assertEquals("Updated Title", book.getTitle());
        assertEquals(39.99f, book.getPrice());
        verify(bookRepository).findById(1);
        verify(bookRepository).save(book);
    }

    @Test
    void updateBookOnlyUpdatesNonNullFields() {
        BookApiDto updateDto = new BookApiDto();
        updateDto.setTitle("Updated Title");
        updateDto.setPrice(0.0f);

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book result = bookService.updateBook(updateDto, 1);

        assertNotNull(result);
        assertEquals("Updated Title", book.getTitle());
        assertEquals(29.99f, book.getPrice()); // Original price preserved
        verify(bookRepository).findById(1);
        verify(bookRepository).save(book);
    }

    @Test
    void updateBookThrowsExceptionWhenNotFound() {
        BookApiDto updateDto = new BookApiDto();
        updateDto.setTitle("Updated Title");

        when(bookRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> bookService.updateBook(updateDto, 999)
        );

        assertEquals("Book not found with id 999", exception.getMessage());
        verify(bookRepository).findById(999);
        verify(bookRepository, never()).save(any());
    }

    @Test
    void deleteBookCallsRepositoryDelete() {
        doNothing().when(bookRepository).deleteById(1);

        bookService.deleteBook(1);

        verify(bookRepository).deleteById(1);
    }

    @Test
    void getDescriptionByIdReturnsDescription() {
        String expectedDescription = "Test Description";
        when(bookRepository.getDescriptionById(1)).thenReturn(expectedDescription);

        String result = bookService.GetDescriptionById(1);

        assertEquals(expectedDescription, result);
        verify(bookRepository).getDescriptionById(1);
    }

    @Test
    void getDescriptionByIdReturnsNullWhenNotFound() {
        when(bookRepository.getDescriptionById(999)).thenReturn(null);

        String result = bookService.GetDescriptionById(999);

        assertNull(result);
        verify(bookRepository).getDescriptionById(999);
    }
}
