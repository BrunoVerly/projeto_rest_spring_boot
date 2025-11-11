package com.example.projetoRestSpringBoot.repository;

import com.example.projetoRestSpringBoot.model.Curso;
import com.example.projetoRestSpringBoot.model.Funcionario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    @Query("SELECT c FROM Curso c WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<Curso> findCursoByName(@Param("nome") String nome, Pageable pageable);
}
