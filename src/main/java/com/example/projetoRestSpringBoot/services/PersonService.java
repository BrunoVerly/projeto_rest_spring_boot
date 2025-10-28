package com.example.projetoRestSpringBoot.services;


import java.util.List;


import com.example.projetoRestSpringBoot.controller.PersonController;
import com.example.projetoRestSpringBoot.data.dto.PersonDTO;
import com.example.projetoRestSpringBoot.exception.RequiredObjectIsNullException;
import com.example.projetoRestSpringBoot.exception.ResourceNotFoundException;
import static com.example.projetoRestSpringBoot.mapper.ObjectMapper.parseListObjects;
import static com.example.projetoRestSpringBoot.mapper.ObjectMapper.parseObject;
import com.example.projetoRestSpringBoot.model.Person;
import com.example.projetoRestSpringBoot.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicLong;


@Service
public class PersonService {

    private final AtomicLong counter = new AtomicLong();
    private Logger logger = LoggerFactory.getLogger(PersonService.class.getName());
    @Autowired
    PersonRepository repository;


    public List<PersonDTO> findAll() {
        logger.info(String.format("Finding all persons"));
        var dto = parseListObjects(repository.findAll(), PersonDTO.class);
        for (PersonDTO dtoPerson : dto) {
            addHateosLinks(dtoPerson);
        }
        return dto;
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
        dto.add(linkTo(methodOn(PersonController.class).create(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(PersonController.class).findAll()).withRel("findAll").withType("DELETE"));
    }


}

