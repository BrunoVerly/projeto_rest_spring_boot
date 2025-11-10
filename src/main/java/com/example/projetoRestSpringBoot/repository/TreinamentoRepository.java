package com.example.projetoRestSpringBoot.repository;

import com.example.projetoRestSpringBoot.dto.TreinamentoDTO;
import com.example.projetoRestSpringBoot.enums.CredencialStatus;
import com.example.projetoRestSpringBoot.enums.TreinamentoStatus;
import com.example.projetoRestSpringBoot.model.Credencial;
import com.example.projetoRestSpringBoot.model.Treinamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TreinamentoRepository extends JpaRepository<Treinamento, Long> {
    @Query("SELECT t FROM Treinamento t WHERE " + "t.dataVencimento BETWEEN " + ":startDate AND :endDate")
    Page<TreinamentoDTO> findTreinamentoExpiring(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate,
                                                 Pageable pageable);

    @Query("SELECT t FROM Treinamento t WHERE " + "t.dataConcluido BETWEEN " + ":startDate AND :endDate")
    Page<TreinamentoDTO> findTreinamentoConluded(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate,
                                                 Pageable pageable);

    @Query("SELECT t FROM Treinamento t WHERE t.funcionario.id = :id AND t.status = :status")
    Page<Treinamento> findTreinamentosByFuncionario(@Param("id") Long id,
                                                    @Param("status") TreinamentoStatus status,
                                                    Pageable pageable);

    Page<TreinamentoDTO> findByStatus(TreinamentoStatus status, Pageable pageable);

    Page<TreinamentoDTO> findByInstrutor(String instrutor, Pageable pageable);

}
