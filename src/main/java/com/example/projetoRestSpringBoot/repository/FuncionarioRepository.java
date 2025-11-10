package com.example.projetoRestSpringBoot.repository;

import com.example.projetoRestSpringBoot.dto.FuncionarioDTO;
import com.example.projetoRestSpringBoot.dto.TreinamentoDTO;
import com.example.projetoRestSpringBoot.enums.FuncionarioSituacao;
import com.example.projetoRestSpringBoot.enums.TreinamentoStatus;
import com.example.projetoRestSpringBoot.model.Funcionario;
import com.example.projetoRestSpringBoot.model.Treinamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    @Query("SELECT f FROM Funcionario f WHERE LOWER(f.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<Funcionario> findFuncionarioByName(@Param("nome") String nome, Pageable pageable);

    @Query("SELECT f FROM Funcionario f WHERE f.dataAdmissao BETWEEN :startDate AND :endDate")
    Page<Funcionario> findFuncionarioByAddmitedDate(@Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate,
                                                    Pageable pageable);

    Optional<Funcionario> findByMatricula(String matricula);

    Page<Funcionario> findBySituacao(FuncionarioSituacao situacao, Pageable pageable);
}

