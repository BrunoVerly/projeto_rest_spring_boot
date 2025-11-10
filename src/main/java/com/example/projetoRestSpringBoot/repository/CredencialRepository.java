package com.example.projetoRestSpringBoot.repository;

import com.example.projetoRestSpringBoot.enums.CredencialStatus;
import com.example.projetoRestSpringBoot.model.Credencial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface CredencialRepository extends JpaRepository<Credencial, Long> {

    @Query("SELECT c FROM Credencial c WHERE c.dataVencimento BETWEEN :startDate AND :endDate")
    Page<Credencial> findCredencialExpiring(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate,
                                            Pageable pageable);

    @Query("SELECT c FROM Credencial c WHERE c.dataEmissao BETWEEN :startDate AND :endDate")
    Page<Credencial> findCredencialEmited(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate,
                                          Pageable pageable);

    Page<Credencial> findByStatus(CredencialStatus status, Pageable pageable);

}

