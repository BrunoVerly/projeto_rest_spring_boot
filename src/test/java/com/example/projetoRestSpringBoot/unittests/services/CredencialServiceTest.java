package com.example.projetoRestSpringBoot.unittests.services;

import com.example.projetoRestSpringBoot.file.exporter.contract.FileExporter;
import com.example.projetoRestSpringBoot.file.exporter.factory.FileExporterFactory;
import com.example.projetoRestSpringBoot.model.Credencial;
import com.example.projetoRestSpringBoot.model.Funcionario;
import com.example.projetoRestSpringBoot.repository.CredencialRepository;
import com.example.projetoRestSpringBoot.repository.FuncionarioRepository;
import com.example.projetoRestSpringBoot.service.CredencialService;
import com.example.projetoRestSpringBoot.unittests.mocks.MockCredencial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import com.example.projetoRestSpringBoot.dto.CredencialDTO;
import com.example.projetoRestSpringBoot.enums.CredencialStatus;
import com.example.projetoRestSpringBoot.enums.CredencialTipo;
import com.example.projetoRestSpringBoot.exception.RequiredObjectIsNullException;
import com.example.projetoRestSpringBoot.exception.ResourceNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class CredencialServiceTest {

    MockCredencial mockCredencial;

    @InjectMocks
    private CredencialService service;

    @Mock
    CredencialRepository repository;

    @Mock
    FuncionarioRepository funcionarioRepository;

    @Mock
    PagedResourcesAssembler<CredencialDTO> assembler;

    @Mock
    FileExporterFactory exporterFactory;

    @BeforeEach
    void setUp() {
        mockCredencial = new MockCredencial();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById() {
        Credencial credencial = mockCredencial.mockEntity(1);
        Long credencialId = credencial.getId();

        when(repository.findById(credencialId)).thenReturn(Optional.of(credencial));

        var result = service.findById(credencialId);

        assertNotNull(result, "Resultado não deve ser nulo");
        assertEquals(credencialId, result.getId(), "ID deve corresponder");
        assertEquals(credencial.getTipo(), result.getTipo(), "Tipo deve corresponder");
        assertEquals(credencial.getDataEmissao(), result.getDataEmissao(), "Data emissão deve corresponder");
        assertEquals(credencial.getDataVencimento(), result.getDataVencimento(), "Data vencimento deve corresponder");
        assertEquals(credencial.getStatus(), result.getStatus(), "Status deve corresponder");
        assertEquals(credencial.getFuncionario().getId(), result.getFuncionarioId(), "ID funcionário deve corresponder");
        assertEquals(credencial.getFuncionario().getNome(), result.getFuncionarioNome(), "Nome funcionário deve corresponder");
        assertEquals(credencial.getFuncionario().getMatricula(), result.getFuncionarioMatricula(), "Matrícula funcionário deve corresponder");

        assertFalse(result.getLinks().isEmpty(), "Links não devem estar vazios");
        assertEquals(5, result.getLinks().stream().count(), "Deve conter 7 links");

        assertTrue(result.getLinks().stream()
                        .anyMatch(link -> "self".equals(link.getRel().value()) && "GET".equals(link.getType())),
                "Deve conter link self com tipo GET");

        assertTrue(result.getLinks().stream()
                        .anyMatch(link -> "delete".equals(link.getRel().value()) && "DELETE".equals(link.getType())),
                "Deve conter link delete com tipo GET");

        assertTrue(result.getLinks().stream()
                        .anyMatch(link -> "update".equals(link.getRel().value()) && "PUT".equals(link.getType())),
                "Deve conter link update com tipo PUT");

        assertTrue(result.getLinks().stream()
                        .anyMatch(link -> "findAll".equals(link.getRel().value()) && "GET".equals(link.getType())),
                "Deve conter link findAll com tipo GET");

        assertTrue(result.getLinks().stream()
                        .anyMatch(link -> "status".equals(link.getRel().value()) && "GET".equals(link.getType())),
                "Deve conter link status com tipo GET");


        verify(repository, times(1)).findById(credencialId);
    }

    @Test
    void findByIdNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(999L),
                "Deve lançar ResourceNotFoundException quando credencial não existe");

        verify(repository, times(1)).findById(999L);
    }

    @Test
    void findAll() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("dataEmissao").ascending());
        List<Credencial> credenciais = List.of(
                mockCredencial.mockEntity(1),
                mockCredencial.mockEntity(2)
        );
        Page<Credencial> page = new PageImpl<>(credenciais, pageable, credenciais.size());

        when(repository.findAll(pageable)).thenReturn(page);

        PagedModel<EntityModel<CredencialDTO>> pagedModel = mock(PagedModel.class);
        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(pagedModel);

        var result = service.findAll(pageable);

        assertNotNull(result, "Resultado não deve ser nulo");
        verify(repository, times(1)).findAll(pageable);
        verify(assembler, times(1)).toModel(any(Page.class), any(Link.class));
    }

    @Test
    void findByStatus() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("dataEmissao").ascending());
        List<Credencial> credenciais = List.of(
                mockCredencial.mockEntity(1),
                mockCredencial.mockEntity(2)
        );
        Page<Credencial> page = new PageImpl<>(credenciais, pageable, credenciais.size());
        CredencialStatus status = CredencialStatus.VALIDA;

        when(repository.findByStatus(eq(status), eq(pageable))).thenReturn(page);

        PagedModel<EntityModel<CredencialDTO>> pagedModel = mock(PagedModel.class);
        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(pagedModel);

        var result = service.findByStatus(status, pageable);

        assertNotNull(result, "Resultado não deve ser nulo");
        verify(repository, times(1)).findByStatus(eq(status), eq(pageable));
        verify(assembler, times(1)).toModel(any(Page.class), any(Link.class));
    }

    @Test
    void findCredencialEmited() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("dataEmissao").ascending());
        List<Credencial> credenciais = List.of(mockCredencial.mockEntity(1));
        Page<Credencial> page = new PageImpl<>(credenciais, pageable, 1);
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        when(repository.findCredencialEmited(eq(startDate), eq(endDate), eq(pageable))).thenReturn(page);

        PagedModel<EntityModel<CredencialDTO>> pagedModel = mock(PagedModel.class);
        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(pagedModel);

        var result = service.findCredencialEmited(startDate, endDate, pageable);

        assertNotNull(result, "Resultado não deve ser nulo");
        verify(repository, times(1)).findCredencialEmited(eq(startDate), eq(endDate), eq(pageable));
        verify(assembler, times(1)).toModel(any(Page.class), any(Link.class));
    }

    @Test
    void findCredencialExpiring() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("dataVencimento").ascending());
        List<Credencial> credenciais = List.of(mockCredencial.mockEntity(1));
        Page<Credencial> page = new PageImpl<>(credenciais, pageable, 1);
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        when(repository.findCredencialExpiring(eq(startDate), eq(endDate), eq(pageable))).thenReturn(page);

        PagedModel<EntityModel<CredencialDTO>> pagedModel = mock(PagedModel.class);
        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(pagedModel);

        var result = service.findCredencialExpiring(startDate, endDate, pageable);

        assertNotNull(result, "Resultado não deve ser nulo");
        verify(repository, times(1)).findCredencialExpiring(eq(startDate), eq(endDate), eq(pageable));
        verify(assembler, times(1)).toModel(any(Page.class), any(Link.class));
    }

    @Test
    void create() {
        Credencial credencial = mockCredencial.mockEntity(1);
        Funcionario funcionario = mock(Funcionario.class);
        funcionario.setId(credencial.getFuncionario().getId());
        credencial.setFuncionario(funcionario);

        when(funcionarioRepository.findById(credencial.getFuncionario().getId())).thenReturn(Optional.of(funcionario));
        when(repository.save(any(Credencial.class))).thenReturn(credencial);

        var result = service.create(credencial);

        assertNotNull(result, "Resultado não deve ser nulo");
        assertEquals(credencial.getId(), result.getId(), "ID deve corresponder");
        assertEquals(credencial.getTipo(), result.getTipo(), "Tipo deve corresponder");

        verify(funcionarioRepository, times(1)).findById(credencial.getFuncionario().getId());
        verify(repository, times(1)).save(any(Credencial.class)); // ← alterar de times(2) para times(1)
    }

    @Test
    void createWithNull() {
        assertThrows(RequiredObjectIsNullException.class, () -> service.create(null),
                "Deve lançar RequiredObjectIsNullException quando credencial é nula");

        verify(repository, times(0)).save(any(Credencial.class));
    }

    @Test
    void createFuncionarioNotFound() {
        Funcionario funcionario = mock(Funcionario.class);
        doReturn(999L).when(funcionario).getId();

        Credencial credencial = new Credencial();
        credencial.setFuncionario(funcionario);
        credencial.setTipo(CredencialTipo.PERMANENTE);
        credencial.setDataEmissao(LocalDate.now());
        credencial.setDataVencimento(LocalDate.now().plusMonths(12));
        credencial.setStatus(CredencialStatus.VALIDA);

        when(funcionarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(credencial),
                "Deve lançar ResourceNotFoundException quando funcionário não existe");

        verify(funcionarioRepository, times(1)).findById(999L);
        verify(repository, times(0)).save(any(Credencial.class));
    }


    @Test
    void update() {
        Funcionario funcionario = mock(Funcionario.class);
        funcionario.setId(1L);
        funcionario.setNome("João Silva");
        funcionario.setMatricula("MAT001");

        Credencial credencial = mockCredencial.mockEntity(1);
        credencial.setFuncionario(funcionario);

        CredencialDTO dto = new CredencialDTO();
        dto.setId(1L);
        dto.setTipo(CredencialTipo.PERMANENTE);
        dto.setDataEmissao(LocalDate.now());
        dto.setDataVencimento(LocalDate.now().plusMonths(12));
        dto.setStatus(CredencialStatus.VALIDA);
        dto.setFuncionarioId(1L);
        dto.setFuncionarioNome("João Silva");
        dto.setFuncionarioMatricula("MAT001");

        when(repository.findById(1L)).thenReturn(Optional.of(credencial));
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));
        when(repository.save(any(Credencial.class))).thenReturn(credencial);

        var result = service.update(dto);

        assertNotNull(result, "Resultado não deve ser nulo");
        assertEquals(1L, result.getId(), "ID deve corresponder");
        assertFalse(result.getLinks().isEmpty(), "Links não devem estar vazios");
        assertEquals(5, result.getLinks().stream().count(), "Deve conter 7 links");

        assertTrue(result.getLinks().stream()
                        .anyMatch(link -> "self".equals(link.getRel().value())),
                "Deve conter link self");

        verify(repository, times(1)).findById(1L);
        verify(funcionarioRepository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Credencial.class));
    }

    @Test
    void updateWithNull() {
        assertThrows(RequiredObjectIsNullException.class, () -> service.update(null),
                "Deve lançar RequiredObjectIsNullException quando credencial é nula");

        verify(repository, times(0)).findById(any());
        verify(repository, times(0)).save(any(Credencial.class));
    }

    @Test
    void updateNotFound() {
        CredencialDTO dto = new CredencialDTO();
        dto.setId(999L);
        dto.setTipo(CredencialTipo.PERMANENTE);           // ← adicionar
        dto.setDataEmissao(LocalDate.now());              // ← adicionar
        dto.setDataVencimento(LocalDate.now().plusYears(1)); // ← adicionar

        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(dto),
                "Deve lançar ResourceNotFoundException quando credencial não existe");

        verify(repository, times(1)).findById(999L);
        verify(repository, times(0)).save(any(Credencial.class));
    }

    @Test
    void delete() {
        Credencial credencial = mockCredencial.mockEntity(1);
        Long credencialId = credencial.getId();

        when(repository.findById(credencialId)).thenReturn(Optional.of(credencial));

        assertDoesNotThrow(() -> service.delete(credencialId),
                "Não deve lançar exceção ao deletar credencial existente");

        verify(repository, times(1)).findById(credencialId);
        verify(repository, times(1)).delete(credencial);
    }

    @Test
    void deleteNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(999L),
                "Deve lançar ResourceNotFoundException quando credencial não existe");

        verify(repository, times(1)).findById(999L);
        verify(repository, times(0)).delete(any(Credencial.class));
    }

    @Test
    void exportPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("dataEmissao").ascending());
        List<Credencial> credenciais = List.of(mockCredencial.mockEntity(1));
        Page<Credencial> page = new PageImpl<>(credenciais, pageable, 1);

        when(repository.findAll(pageable)).thenReturn(page);

        FileExporter exporter = mock(FileExporter.class);
        Resource resource = mock(Resource.class);
        when(exporterFactory.getExporter("application/pdf")).thenReturn(exporter);
        when(exporter.exportarCredenciais(any(List.class))).thenReturn(resource);

        var result = service.exportPage(pageable, "application/pdf");

        assertNotNull(result, "Resultado não deve ser nulo");
        verify(repository, times(1)).findAll(pageable);
        verify(exporterFactory, times(1)).getExporter("application/pdf");
        verify(exporter, times(1)).exportarCredenciais(any(List.class));
    }

    @Test
    void exportPageWithInvalidFormat() throws Exception {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("dataEmissao").ascending());
        List<Credencial> credenciais = List.of(mockCredencial.mockEntity(1));
        Page<Credencial> page = new PageImpl<>(credenciais, pageable, 1);

        when(repository.findAll(pageable)).thenReturn(page);
        when(exporterFactory.getExporter("text/invalid")).thenThrow(new RuntimeException("Formato inválido"));

        assertThrows(RuntimeException.class, () -> service.exportPage(pageable, "text/invalid"),
                "Deve lançar RuntimeException quando formato é inválido");

        verify(repository, times(1)).findAll(pageable);
        verify(exporterFactory, times(1)).getExporter("text/invalid");
    }

    @Test
    void atualizarStatusCredenciais() {
        Credencial credencialValida = mockCredencial.mockEntity(1);
        credencialValida.setDataVencimento(LocalDate.now().plusMonths(6));

        Credencial credencialProxima = mockCredencial.mockEntity(2);
        credencialProxima.setDataVencimento(LocalDate.now().plusDays(30));

        Credencial credencialVencida = mockCredencial.mockEntity(3);
        credencialVencida.setDataVencimento(LocalDate.now().minusDays(1));

        List<Credencial> credenciais = List.of(credencialValida, credencialProxima, credencialVencida);

        when(repository.findAll()).thenReturn(credenciais);

        assertDoesNotThrow(() -> service.atualizarStatusCredenciais(),
                "Não deve lançar exceção ao atualizar status de credenciais");

        verify(repository, times(1)).findAll();
        verify(repository, times(1)).saveAll(credenciais);
    }
}
