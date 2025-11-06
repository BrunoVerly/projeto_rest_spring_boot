package com.example.projetoRestSpringBoot.unittests.services;

import com.example.projetoRestSpringBoot.data.dto.BookDTO;
import com.example.projetoRestSpringBoot.exception.RequiredObjectIsNullException;
import com.example.projetoRestSpringBoot.model.Book;
import com.example.projetoRestSpringBoot.repository.BookRepository;
import com.example.projetoRestSpringBoot.services.BookService;
import com.example.projetoRestSpringBoot.unittests.mapper.mocks.MockBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    MockBook input;
    @InjectMocks
    private BookService service;

    @Mock
    BookRepository repository;



    @BeforeEach
    void setUp() {
        input = new MockBook();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById() {
        Book book = input.mockEntity(1);
        book.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(book));
        var result = service.findById(1L);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("self")
                && link.getHref().endsWith("/api/book/v1/1")
                && link.getType().equals("GET"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("findlAll")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("GET"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("create")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("POST"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("update")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("PUT"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("delete")
                        && link.getHref().endsWith("/api/book/v1/1")
                        && link.getType().equals("DELETE"))
        );

        assertEquals("Author1", result.getAuthor());
        assertEquals(25D, result.getPrice());
        assertEquals("Title1", result.getTitle());
        assertNotNull(result.getLaunchDate());

    }

    @Test
    void create() {
        Book book = input.mockEntity(1);
        Book persisted = book;
        persisted.setId(1L);
        BookDTO dto = input.mockDTO(1);

        when(repository.save(book)).thenReturn(persisted);
        var result = service.create(dto);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("self")
                        && link.getHref().endsWith("/api/book/v1/1")
                        && link.getType().equals("GET"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("findlAll")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("GET"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("create")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("POST"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("update")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("PUT"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("delete")
                        && link.getHref().endsWith("/api/book/v1/1")
                        && link.getType().equals("DELETE"))
        );

        assertEquals("Author1", result.getAuthor());
        assertEquals(25D, result.getPrice());
        assertEquals("Title1", result.getTitle());
        assertNotNull(result.getLaunchDate());
    }

    @Test
    void testCreateWithNullBook() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class,
                () -> {
                    service.create(null);
                });

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void update() {
        Book book = input.mockEntity(1);
        Book persisted = book;
        persisted.setId(1L);
        BookDTO dto = input.mockDTO(1);

        when(repository.findById(1L)).thenReturn(Optional.of(book));
        when(repository.save(book)).thenReturn(persisted);
        var result = service.update(dto);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("self")
                        && link.getHref().endsWith("/api/book/v1/1")
                        && link.getType().equals("GET"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("findlAll")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("GET"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("create")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("POST"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("update")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("PUT"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("delete")
                        && link.getHref().endsWith("/api/book/v1/1")
                        && link.getType().equals("DELETE"))
        );

        assertEquals("Author1", result.getAuthor());
        assertEquals(25D, result.getPrice());
        assertEquals("Title1", result.getTitle());
        assertNotNull(result.getLaunchDate());
    }

    @Test
    void testUpdateWithNullBook() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class,
                () -> {
                    service.update(null);
                });

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void delete() {
        Book book = input.mockEntity(1);
        book.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(book));
        service.delete(1L);

        verify(repository, times(1)).findById(anyLong());
        verify(repository, times(1)).delete(any(Book.class));
        verifyNoMoreInteractions(repository);

    }
    @Test
    @Disabled("REASON: Not yet implemented")
    void findAll() {
        List<Book> list = input.mockEntityList();
        when(repository.findAll()).thenReturn(list);
        List<BookDTO> books = new ArrayList<>(); // service.findAll();

        assertNotNull(books);
        assertEquals(14, books.size());

        var book1 = books.get(1);

        assertNotNull(book1);
        assertNotNull(book1.getId());
        assertNotNull(book1.getLinks());
        assertNotNull(book1.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("self")
                        && link.getHref().endsWith("/api/book/v1/1")
                        && link.getType().equals("GET"))
        );

        assertNotNull(book1);
        assertNotNull(book1.getId());
        assertNotNull(book1.getLinks());
        assertNotNull(book1.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("findlAll")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("GET"))
        );

        assertNotNull(book1);
        assertNotNull(book1.getId());
        assertNotNull(book1.getLinks());
        assertNotNull(book1.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("create")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("POST"))
        );

        assertNotNull(book1);
        assertNotNull(book1.getId());
        assertNotNull(book1.getLinks());
        assertNotNull(book1.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("update")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("PUT"))
        );

        assertNotNull(book1);
        assertNotNull(book1.getId());
        assertNotNull(book1.getLinks());
        assertNotNull(book1.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("delete")
                        && link.getHref().endsWith("/api/book/v1/1")
                        && link.getType().equals("DELETE"))
        );

        assertEquals("Author1", book1.getAuthor());
        assertEquals(25D, book1.getPrice());
        assertEquals("Title1", book1.getTitle());
        assertNotNull(book1.getLaunchDate());

        var book6= books.get(6);

        assertNotNull(book6);
        assertNotNull(book6.getId());
        assertNotNull(book6.getLinks());
        assertNotNull(book6.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("self")
                        && link.getHref().endsWith("/api/book/v1/6")
                        && link.getType().equals("GET"))
        );

        assertNotNull(book6);
        assertNotNull(book6.getId());
        assertNotNull(book6.getLinks());
        assertNotNull(book6.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("findlAll")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("GET"))
        );

        assertNotNull(book6);
        assertNotNull(book6.getId());
        assertNotNull(book6.getLinks());
        assertNotNull(book6.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("create")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("POST"))
        );

        assertNotNull(book6);
        assertNotNull(book6.getId());
        assertNotNull(book6.getLinks());
        assertNotNull(book6.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("update")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("PUT"))
        );

        assertNotNull(book6);
        assertNotNull(book6.getId());
        assertNotNull(book6.getLinks());
        assertNotNull(book6.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("delete")
                        && link.getHref().endsWith("/api/book/v1/6")
                        && link.getType().equals("DELETE"))
        );

        assertEquals("Author6", book6.getAuthor());
        assertEquals(25D, book6.getPrice());
        assertEquals("Title6", book6.getTitle());
        assertNotNull(book6.getLaunchDate());


        var book13= books.get(13);

        assertNotNull(book13);
        assertNotNull(book13.getId());
        assertNotNull(book13.getLinks());
        assertNotNull(book13.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("self")
                        && link.getHref().endsWith("/api/book/v1/13")
                        && link.getType().equals("GET"))
        );

        assertNotNull(book13);
        assertNotNull(book13.getId());
        assertNotNull(book13.getLinks());
        assertNotNull(book13.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("findlAll")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("GET"))
        );

        assertNotNull(book13);
        assertNotNull(book13.getId());
        assertNotNull(book13.getLinks());
        assertNotNull(book13.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("create")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("POST"))
        );

        assertNotNull(book13);
        assertNotNull(book13.getId());
        assertNotNull(book13.getLinks());
        assertNotNull(book13.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("update")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("PUT"))
        );

        assertNotNull(book13);
        assertNotNull(book13.getId());
        assertNotNull(book13.getLinks());
        assertNotNull(book13.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("delete")
                        && link.getHref().endsWith("/api/book/v1/13")
                        && link.getType().equals("DELETE"))
        );

        assertEquals("Author13", book13.getAuthor());
        assertEquals(25D, book13.getPrice());
        assertEquals("Title13", book13.getTitle());
        assertNotNull(book13.getLaunchDate());



    }
}