package com.example.projetoRestSpringBoot.services;

import com.example.projetoRestSpringBoot.data.dto.PersonDTO;
import com.example.projetoRestSpringBoot.exception.RequiredObjectIsNullException;
import com.example.projetoRestSpringBoot.model.Person;
import com.example.projetoRestSpringBoot.repository.PersonRepository;
import com.example.projetoRestSpringBoot.unitetests.mapper.mocks.MockPerson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    MockPerson input;
    @InjectMocks
    private PersonService service;

    @Mock
    PersonRepository repository;



    @BeforeEach
    void setUp() {
        input = new MockPerson();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById() {
        Person person = input.mockEntity(1);
        person.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(person));
        var result = service.findById(1L);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("self")
                && link.getHref().endsWith("/api/person/v1/1")
                && link.getType().equals("GET"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("findlAll")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("GET"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("create")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("POST"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("update")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("PUT"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("delete")
                        && link.getHref().endsWith("/api/person/v1/1")
                        && link.getType().equals("DELETE"))
        );

        assertEquals("Address Test1", result.getAddress());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Female", result.getGender());
        assertEquals("Last Name Test1", result.getLastName());

    }

    @Test
    void create() {
        Person person = input.mockEntity(1);
        Person persisted = person;
        persisted.setId(1L);
        PersonDTO dto = input.mockDTO(1);

        when(repository.save(person)).thenReturn(persisted);
        var result = service.create(dto);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("self")
                        && link.getHref().endsWith("/api/person/v1/1")
                        && link.getType().equals("GET"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("findlAll")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("GET"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("create")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("POST"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("update")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("PUT"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("delete")
                        && link.getHref().endsWith("/api/person/v1/1")
                        && link.getType().equals("DELETE"))
        );

        assertEquals("Address Test1", result.getAddress());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Female", result.getGender());
        assertEquals("Last Name Test1", result.getLastName());
    }

    @Test
    void testCreateWithNullPerson() {
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
        Person person = input.mockEntity(1);
        Person persisted = person;
        persisted.setId(1L);
        PersonDTO dto = input.mockDTO(1);

        when(repository.findById(1L)).thenReturn(Optional.of(person));
        when(repository.save(person)).thenReturn(persisted);
        var result = service.update(dto);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("self")
                        && link.getHref().endsWith("/api/person/v1/1")
                        && link.getType().equals("GET"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("findlAll")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("GET"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("create")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("POST"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("update")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("PUT"))
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("delete")
                        && link.getHref().endsWith("/api/person/v1/1")
                        && link.getType().equals("DELETE"))
        );

        assertEquals("Address Test1", result.getAddress());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Female", result.getGender());
        assertEquals("Last Name Test1", result.getLastName());
    }

    @Test
    void testUpdateWithNullPerson() {
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
        Person person = input.mockEntity(1);
        person.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(person));
        service.delete(1L);

        verify(repository, times(1)).findById(anyLong());
        verify(repository, times(1)).delete(any(Person.class));
        verifyNoMoreInteractions(repository);

    }
    @Test
    void findAll() {
        List<Person> list = input.mockEntityList();
        when(repository.findAll()).thenReturn(list);
        List<PersonDTO> persons = service.findAll();

        assertNotNull(persons);
        assertEquals(14, persons.size());

        var person1 = persons.get(1);

        assertNotNull(person1);
        assertNotNull(person1.getId());
        assertNotNull(person1.getLinks());
        assertNotNull(person1.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("self")
                        && link.getHref().endsWith("/api/person/v1/1")
                        && link.getType().equals("GET"))
        );

        assertNotNull(person1);
        assertNotNull(person1.getId());
        assertNotNull(person1.getLinks());
        assertNotNull(person1.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("findlAll")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("GET"))
        );

        assertNotNull(person1);
        assertNotNull(person1.getId());
        assertNotNull(person1.getLinks());
        assertNotNull(person1.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("create")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("POST"))
        );

        assertNotNull(person1);
        assertNotNull(person1.getId());
        assertNotNull(person1.getLinks());
        assertNotNull(person1.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("update")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("PUT"))
        );

        assertNotNull(person1);
        assertNotNull(person1.getId());
        assertNotNull(person1.getLinks());
        assertNotNull(person1.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("delete")
                        && link.getHref().endsWith("/api/person/v1/1")
                        && link.getType().equals("DELETE"))
        );

        assertEquals("Address Test1", person1.getAddress());
        assertEquals("First Name Test1", person1.getFirstName());
        assertEquals("Female", person1.getGender());
        assertEquals("Last Name Test1", person1.getLastName());

        var person6= persons.get(6);

        assertNotNull(person6);
        assertNotNull(person6.getId());
        assertNotNull(person6.getLinks());
        assertNotNull(person6.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("self")
                        && link.getHref().endsWith("/api/person/v1/6")
                        && link.getType().equals("GET"))
        );

        assertNotNull(person6);
        assertNotNull(person6.getId());
        assertNotNull(person6.getLinks());
        assertNotNull(person6.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("findlAll")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("GET"))
        );

        assertNotNull(person6);
        assertNotNull(person6.getId());
        assertNotNull(person6.getLinks());
        assertNotNull(person6.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("create")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("POST"))
        );

        assertNotNull(person6);
        assertNotNull(person6.getId());
        assertNotNull(person6.getLinks());
        assertNotNull(person6.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("update")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("PUT"))
        );

        assertNotNull(person6);
        assertNotNull(person6.getId());
        assertNotNull(person6.getLinks());
        assertNotNull(person6.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("delete")
                        && link.getHref().endsWith("/api/person/v1/6")
                        && link.getType().equals("DELETE"))
        );

        assertEquals("Address Test6", person6.getAddress());
        assertEquals("First Name Test6", person6.getFirstName());
        assertEquals("Male", person6.getGender());
        assertEquals("Last Name Test6", person6.getLastName());


        var person13= persons.get(13);

        assertNotNull(person13);
        assertNotNull(person13.getId());
        assertNotNull(person13.getLinks());
        assertNotNull(person13.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("self")
                        && link.getHref().endsWith("/api/person/v1/13")
                        && link.getType().equals("GET"))
        );

        assertNotNull(person13);
        assertNotNull(person13.getId());
        assertNotNull(person13.getLinks());
        assertNotNull(person13.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("findlAll")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("GET"))
        );

        assertNotNull(person13);
        assertNotNull(person13.getId());
        assertNotNull(person13.getLinks());
        assertNotNull(person13.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("create")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("POST"))
        );

        assertNotNull(person13);
        assertNotNull(person13.getId());
        assertNotNull(person13.getLinks());
        assertNotNull(person13.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("update")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("PUT"))
        );

        assertNotNull(person13);
        assertNotNull(person13.getId());
        assertNotNull(person13.getLinks());
        assertNotNull(person13.getLinks().stream()
                .anyMatch(link -> link.getRel().equals("delete")
                        && link.getHref().endsWith("/api/person/v1/13")
                        && link.getType().equals("DELETE"))
        );

        assertEquals("Address Test13", person13.getAddress());
        assertEquals("First Name Test13", person13.getFirstName());
        assertEquals("Female", person13.getGender());
        assertEquals("Last Name Test13", person13.getLastName());



    }
}