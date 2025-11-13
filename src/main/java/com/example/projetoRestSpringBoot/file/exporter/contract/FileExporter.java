package com.example.projetoRestSpringBoot.file.exporter.contract;

import com.example.projetoRestSpringBoot.dto.CredencialDTO;
import com.example.projetoRestSpringBoot.dto.CursoDTO;
import com.example.projetoRestSpringBoot.dto.FuncionarioDTO;
import com.example.projetoRestSpringBoot.dto.TreinamentoDTO;
import org.springframework.core.io.Resource;
import java.util.List;

public interface FileExporter {
    Resource exportarFuncionarios (List<FuncionarioDTO> funcionarios) throws Exception;
    Resource exportarCredenciais (List<CredencialDTO> credenciais) throws Exception;
    Resource exportTreinamentos (List<TreinamentoDTO> treinamentos) throws Exception;
    Resource exportCursos (List<CursoDTO> cursos) throws Exception;
    Resource exportTreinamentoPorId(TreinamentoDTO treinamento, long funcionarioId) throws Exception;

}
