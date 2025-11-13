package com.example.projetoRestSpringBoot.file.exporter.impl;

import com.example.projetoRestSpringBoot.dto.CredencialDTO;
import com.example.projetoRestSpringBoot.dto.CursoDTO;
import com.example.projetoRestSpringBoot.dto.FuncionarioDTO;
import com.example.projetoRestSpringBoot.dto.TreinamentoDTO;
import com.example.projetoRestSpringBoot.exception.BadRequestException;
import com.example.projetoRestSpringBoot.exception.FileStorageException;
import com.example.projetoRestSpringBoot.exception.RequiredObjectIsNullException;
import com.example.projetoRestSpringBoot.exception.ResourceNotFoundException;
import com.example.projetoRestSpringBoot.file.exporter.contract.FileExporter;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PdfExporter implements FileExporter {
    @Autowired
    private javax.sql.DataSource dataSource;

    @Override
    public Resource exportTreinamentoPorId(TreinamentoDTO treinamento, long funcionarioId) throws Exception {
        // Validações iniciais
        if (funcionarioId <= 0) {
            throw new BadRequestException("ID do funcionário deve ser maior que zero");
        }

        if (treinamento == null) {
            throw new RequiredObjectIsNullException("Treinamento não pode ser nulo");
        }

        if (treinamento.getId() == null || treinamento.getId() <= 0) {
            throw new BadRequestException("ID do treinamento inválido ou ausente");
        }

        Connection connection = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Validar se funcionário existe no banco
            connection = dataSource.getConnection();
            if (!funcionarioExists(connection, funcionarioId)) {
                throw new ResourceNotFoundException("Funcionário não encontrado no banco de dados");
            }

            // Validar se treinamento existe no banco
            if (!treinamentoExists(connection, treinamento.getId(), funcionarioId)) {
                throw new ResourceNotFoundException("Treinamento não encontrado para este funcionário");
            }

            InputStream inputStream = this.getClass().getResourceAsStream("/templates/treinamento_report.jasper");

            if (inputStream == null) {
                throw new FileStorageException("Template do relatório não encontrado");
            }

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputStream);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ID_FUNCIONARIO", funcionarioId);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameters,
                    connection
            );

            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            return new ByteArrayResource(outputStream.toByteArray());

        } catch (FileNotFoundException e) {
            throw new FileStorageException("Arquivo de template não encontrado: " + e.getMessage(), e);
        } catch (JRException e) {
            throw new FileStorageException("Erro ao gerar relatório JasperReports: " + e.getMessage(), e);
        } catch (BadRequestException | RequiredObjectIsNullException | ResourceNotFoundException | FileStorageException e) {
            throw e;
        } catch (Exception e) {
            throw new FileStorageException("Erro inesperado ao exportar PDF: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    // Log silencioso
                }
            }
        }
    }

    private boolean funcionarioExists(Connection connection, long funcionarioId) throws Exception {
        String sql = "SELECT 1 FROM projeto_rest_spring_boot.funcionario WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, funcionarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean treinamentoExists(Connection connection, long treinamentoId, long funcionarioId) throws Exception {
        String sql = "SELECT 1 FROM projeto_rest_spring_boot.treinamento WHERE id = ? AND funcionario_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, treinamentoId);
            stmt.setLong(2, funcionarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public Resource exportarFuncionarios(List<FuncionarioDTO> funcionarios) throws Exception {
        return null;
    }

    @Override
    public Resource exportarCredenciais(List<CredencialDTO> credenciais) throws Exception {
        return null;
    }

    @Override
    public Resource exportTreinamentos(List<TreinamentoDTO> treinamentos) throws Exception {
        return null;
    }

    @Override
    public Resource exportCursos(List<CursoDTO> cursos) throws Exception {
        return null;
    }
}



