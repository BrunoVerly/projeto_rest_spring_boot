package com.example.projetoRestSpringBoot.repository;

import com.example.projetoRestSpringBoot.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
}
