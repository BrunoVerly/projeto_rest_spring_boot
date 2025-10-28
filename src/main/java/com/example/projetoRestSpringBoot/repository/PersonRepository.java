package com.example.projetoRestSpringBoot.repository;

import com.example.projetoRestSpringBoot.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
