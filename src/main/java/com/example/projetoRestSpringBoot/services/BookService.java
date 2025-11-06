package com.example.projetoRestSpringBoot.services;


import com.example.projetoRestSpringBoot.controller.BookController;
import com.example.projetoRestSpringBoot.controller.PersonController;
import com.example.projetoRestSpringBoot.data.dto.BookDTO;
import com.example.projetoRestSpringBoot.exception.RequiredObjectIsNullException;
import com.example.projetoRestSpringBoot.exception.ResourceNotFoundException;
import com.example.projetoRestSpringBoot.model.Book;
import com.example.projetoRestSpringBoot.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicLong;
import static com.example.projetoRestSpringBoot.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Service
public class BookService {

    private final AtomicLong counter = new AtomicLong();
    private Logger logger = LoggerFactory.getLogger(BookService.class.getName());
    @Autowired
    BookRepository repository;

    @Autowired(required = false)
    PagedResourcesAssembler<BookDTO> assembler;

    public PagedModel<EntityModel<BookDTO>> findAll(Pageable pageable) {
        logger.info(String.format("Finding all books"));

        var books = repository.findAll(pageable);
        var bookWithLinks = books.map(dto -> {
            var book = parseObject(dto, BookDTO.class);
            addHateosLinks(book);
            return book;
        } );
        Link findAllLink = WebMvcLinkBuilder.linkTo(
                methodOn(PersonController.class).findAll(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        String.valueOf(pageable.getSort())
                )
        ).withSelfRel();
        return assembler.toModel(bookWithLinks, findAllLink);
    }


    public BookDTO findById(long id) {
        logger.info(String.format("Finding one book"));
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id nao encontrado"));

        var dto = parseObject(entity, BookDTO.class);
        addHateosLinks(dto);
        return dto;
    }

    public BookDTO create(BookDTO book) {

        if(book == null) throw new RequiredObjectIsNullException();

        var entity = parseObject(book, Book.class);
        logger.info(String.format("Creating one book"));
        var dto = parseObject(repository.save(entity), BookDTO.class);
        addHateosLinks(dto);
        return dto;
    }
    public BookDTO update(BookDTO book) {
        if(book == null) throw new RequiredObjectIsNullException();

        Book entity = repository.findById(book.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Id nao encontrado"));

        entity.setAuthor(book.getAuthor());
        entity.setLaunchDate(book.getLaunchDate());
        entity.setPrice(book.getPrice());
        entity.setTitle(book.getTitle());
        var dto = parseObject(repository.save(entity), BookDTO.class);
        addHateosLinks(dto);
        return dto;

    }

    public void delete(long id) {
        logger.info(String.format("Deleting one book"));
        Book entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id nao encontrado"));
        repository.delete(entity);
    }

    private static void addHateosLinks(BookDTO dto) {
        dto.add(linkTo(methodOn(BookController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(BookController.class).delete(dto.getId())).withRel("delete").withType("GET"));
        dto.add(linkTo(methodOn(BookController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(BookController.class).create(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(BookController.class).findAll(1, 12, "asc")).withRel("findAll").withType("DELETE"));
    }


}

