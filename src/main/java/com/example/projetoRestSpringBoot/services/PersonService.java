package com.example.projetoRestSpringBoot.services;


import com.example.projetoRestSpringBoot.controller.PersonController;
import com.example.projetoRestSpringBoot.data.dto.PersonDTO;
import com.example.projetoRestSpringBoot.exception.RequiredObjectIsNullException;
import com.example.projetoRestSpringBoot.exception.ResourceNotFoundException;
import static com.example.projetoRestSpringBoot.mapper.ObjectMapper.parseObject;
import com.example.projetoRestSpringBoot.model.Person;
import com.example.projetoRestSpringBoot.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicLong;


@Service
public class PersonService {

    private final AtomicLong counter = new AtomicLong();
    private Logger logger = LoggerFactory.getLogger(PersonService.class.getName());
    @Autowired
    PersonRepository repository;

    @Autowired(required = false)
    PagedResourcesAssembler<PersonDTO> assembler;

    public PagedModel<EntityModel<PersonDTO>> findAll(Pageable pageable) {
        logger.info(String.format("Finding all persons"));

        var people = repository.findAll(pageable);
        org.springframework.data.domain.Page<PersonDTO> peopleWithLinks = people.map(dto -> {
            PersonDTO person = parseObject(dto, PersonDTO.class);
            addHateosLinks(person);
            return person;
        });
        Link findAllLink = WebMvcLinkBuilder.linkTo(
                methodOn(PersonController.class).findAll(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        String.valueOf(pageable.getSort())
                )
        ).withSelfRel();
        return assembler.toModel(peopleWithLinks, findAllLink);
    }

    public PagedModel<EntityModel<PersonDTO>> findByName(String firstName, Pageable pageable) {
        logger.info(String.format("Finding person by name"));

        var people = repository.findPersonByName(firstName, pageable);
        var peopleWithLinks = people.map(dto -> {
            var person = parseObject(dto, PersonDTO.class);
            addHateosLinks(person);
            return person;
        } );
        Link findAllLink = WebMvcLinkBuilder.linkTo(
                methodOn(PersonController.class).findAll(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        String.valueOf(pageable.getSort())
                )
        ).withSelfRel();
        return assembler.toModel(peopleWithLinks, findAllLink);
    }



    public PersonDTO findById(long id) {
        logger.info(String.format("Finding one person"));
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id nao encontrado"));

        var dto = parseObject(entity, PersonDTO.class);
        addHateosLinks(dto);
        return dto;
    }

    public PersonDTO create(PersonDTO person) {

        if(person == null) throw new RequiredObjectIsNullException();

        var entity = parseObject(person, Person.class);
        logger.info(String.format("Creating one person"));
        var dto = parseObject(repository.save(entity), PersonDTO.class);
        addHateosLinks(dto);
        return dto;
    }
    public PersonDTO update(PersonDTO person) {
        if(person == null) throw new RequiredObjectIsNullException();

        Person entity = repository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Id nao encontrado"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());
        var dto = parseObject(repository.save(entity), PersonDTO.class);
        addHateosLinks(dto);
        return dto;

    }

    @Transactional
    public PersonDTO disblePerson(long id) {
        logger.info(String.format("Disabling one person"));
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id nao encontrado"));
        repository.disablePerson(id);
        var entity = repository.findById(id).get();
        var dto = parseObject(entity, PersonDTO.class);
        addHateosLinks(dto);
        return dto;

    }

    public void delete(long id) {
        logger.info(String.format("Deleting one person"));
        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id nao encontrado"));
        repository.delete(entity);
    }

    private static void addHateosLinks(PersonDTO dto) {
        dto.add(linkTo(methodOn(PersonController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).delete(dto.getId())).withRel("delete").withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(PersonController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(PersonController.class).disablePerson(dto.getId())).withRel("disable").withType("PATCH"));
        dto.add(linkTo(methodOn(PersonController.class).findAll(1, 12, "asc")).withRel("findAll").withType("DELETE"));
    }


}

