package com.example.projetoRestSpringBoot.unittests.services;

import com.example.projetoRestSpringBoot.file.exporter.contract.FileExporter;
import com.example.projetoRestSpringBoot.file.exporter.factory.FileExporterFactory;
import com.example.projetoRestSpringBoot.model.Treinamento;
import com.example.projetoRestSpringBoot.model.Funcionario;
import com.example.projetoRestSpringBoot.model.Curso;
import com.example.projetoRestSpringBoot.repository.TreinamentoRepository;
import com.example.projetoRestSpringBoot.repository.FuncionarioRepository;
import com.example.projetoRestSpringBoot.repository.CursoRepository;
import com.example.projetoRestSpringBoot.services.TreinamentoService;
import com.example.projetoRestSpringBoot.unittests.mocks.MockTreinamento;
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
import com.example.projetoRestSpringBoot.dto.TreinamentoDTO;
import com.example.projetoRestSpringBoot.enums.TreinamentoStatus;
import com.example.projetoRestSpringBoot.exception.RequiredObjectIsNullException;
import com.example.projetoRestSpringBoot.exception.ResourceNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class TreinamentoServiceTest {

    MockTreinamento mockTreinamento;

    @InjectMocks
    private TreinamentoService service;

    @Mock
    TreinamentoRepository repository;

    @Mock
    FuncionarioRepository funcionarioRepository;

    @Mock
    CursoRepository cursoRepository;

    @Mock
    PagedResourcesAssembler<TreinamentoDTO> assembler;

    @Mock
    FileExporterFactory exporterFactory;

    @BeforeEach
    void setUp() {
        mockTreinamento = new MockTreinamento();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById() {
        Treinamento treinamento = mockTreinamento.mockEntity(1);
        when(repository.findById(1L)).thenReturn(Optional.of(treinamento));

        var result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(treinamento.getDataAgendamento(), result.getDataAgendamento());
        assertEquals(treinamento.getDataConcluido(), result.getDataConcluido());
        assertEquals(treinamento.getDataVencimento(), result.getDataVencimento());
        assertEquals(treinamento.getInstrutor(), result.getInstrutor());
        assertEquals(treinamento.getStatus(), result.getStatus());
        assertEquals(treinamento.getFuncionario().getId(), result.getFuncionarioId());
        assertEquals(treinamento.getCurso().getId(), result.getCursoId());

        // Validação completa dos links HATEOAS
        assertFalse(result.getLinks().isEmpty(), "Links HATEOAS não devem estar vazios");
        assertEquals(5, result.getLinks().stream().count(), "Deve conter exatamente 5 links HATEOAS");

        assertTrue(result.getLinks().stream()
                        .anyMatch(link -> link.getRel().value().equals("self")
                                && link.getType().equals("GET")),
                "Link self com tipo GET deve existir");

        assertTrue(result.getLinks().stream()
                        .anyMatch(link -> link.getRel().value().equals("delete")
                                && link.getType().equals("GET")),
                "Link delete com tipo GET deve existir");

        assertTrue(result.getLinks().stream()
                        .anyMatch(link -> link.getRel().value().equals("create")
                                && link.getType().equals("POST")),
                "Link create com tipo POST deve existir");

        assertTrue(result.getLinks().stream()
                        .anyMatch(link -> link.getRel().value().equals("update")
                                && link.getType().equals("PUT")),
                "Link update com tipo PUT deve existir");

        assertTrue(result.getLinks().stream()
                        .anyMatch(link -> link.getRel().value().equals("exportPage")
                                && link.getType().equals("GET")),
                "Link exportPage com tipo GET deve existir");

        verify(repository, times(1)).findById(1L);
    }

    @Test
    void findByIdNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(999L));

        verify(repository, times(1)).findById(999L);
    }

    @Test
    void findAll() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("dataAgendamento").ascending());
        List<Treinamento> treinamentos = List.of(
                mockTreinamento.mockEntity(1),
                mockTreinamento.mockEntity(2),
                mockTreinamento.mockEntity(3)
        );
        Page<Treinamento> page = new PageImpl<>(treinamentos, pageable, treinamentos.size());
        when(repository.findAll(pageable)).thenReturn(page);

        PagedModel<EntityModel<TreinamentoDTO>> pagedModel = mock(PagedModel.class);
        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(pagedModel);

        var result = service.findAll(pageable);

        assertNotNull(result, "Resultado não deve ser nulo");
        assertEquals(pagedModel, result, "Deve retornar o PagedModel do assembler");

        verify(repository, times(1)).findAll(pageable);
        verify(assembler, times(1)).toModel(any(Page.class), any(Link.class));
    }

    @Test
    void findByInstrutor() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("dataAgendamento").ascending());
        Treinamento treinamento = mockTreinamento.mockEntity(1);
        treinamento.setInstrutor("João Silva");
        Page<Treinamento> page = new PageImpl<>(List.of(treinamento), pageable, 1);
        when(repository.findByInstrutor("João Silva", pageable)).thenReturn(page);

        PagedModel<EntityModel<TreinamentoDTO>> pagedModel = mock(PagedModel.class);
        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(pagedModel);

        var result = service.findByInstrutor("João Silva", pageable);

        assertNotNull(result, "Resultado não deve ser nulo");
        assertEquals(pagedModel, result, "Deve retornar o PagedModel do assembler");

        verify(repository, times(1)).findByInstrutor("João Silva", pageable);
        verify(assembler, times(1)).toModel(any(Page.class), any(Link.class));
    }

    @Test
    void findByStatus() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("dataAgendamento").ascending());
        List<Treinamento> treinamentos = List.of(
                mockTreinamento.mockEntity(1),
                mockTreinamento.mockEntity(2)
        );
        treinamentos.forEach(t -> t.setStatus(TreinamentoStatus.VALIDO));
        Page<Treinamento> page = new PageImpl<>(treinamentos, pageable, treinamentos.size());
        when(repository.findByStatus(TreinamentoStatus.VALIDO, pageable)).thenReturn(page);

        PagedModel<EntityModel<TreinamentoDTO>> pagedModel = mock(PagedModel.class);
        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(pagedModel);

        var result = service.findByStatus(TreinamentoStatus.VALIDO, pageable);

        assertNotNull(result, "Resultado não deve ser nulo");
        assertEquals(pagedModel, result, "Deve retornar o PagedModel do assembler");

        verify(repository, times(1)).findByStatus(TreinamentoStatus.VALIDO, pageable);
        verify(assembler, times(1)).toModel(any(Page.class), any(Link.class));
    }

    @Test
    void findTreinamentoExpiring() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("dataVencimento").ascending());
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        List<Treinamento> treinamentos = List.of(
                mockTreinamento.mockEntity(1),
                mockTreinamento.mockEntity(2)
        );
        Page<Treinamento> page = new PageImpl<>(treinamentos, pageable, treinamentos.size());
        when(repository.findTreinamentoExpiring(startDate, endDate, pageable)).thenReturn(page);

        PagedModel<EntityModel<TreinamentoDTO>> pagedModel = mock(PagedModel.class);
        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(pagedModel);

        var result = service.findTreinamentoExpiring(startDate, endDate, pageable);

        assertNotNull(result, "Resultado não deve ser nulo");
        assertEquals(pagedModel, result, "Deve retornar o PagedModel do assembler");

        verify(repository, times(1)).findTreinamentoExpiring(startDate, endDate, pageable);
        verify(assembler, times(1)).toModel(any(Page.class), any(Link.class));
    }

    @Test
    void findTreinamentoConluded() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("dataConcluido").ascending());
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        List<Treinamento> treinamentos = List.of(
                mockTreinamento.mockEntity(1),
                mockTreinamento.mockEntity(2)
        );
        Page<Treinamento> page = new PageImpl<>(treinamentos, pageable, treinamentos.size());
        when(repository.findTreinamentoConluded(startDate, endDate, pageable)).thenReturn(page);

        PagedModel<EntityModel<TreinamentoDTO>> pagedModel = mock(PagedModel.class);
        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(pagedModel);

        var result = service.findTreinamentoConluded(startDate, endDate, pageable);

        assertNotNull(result, "Resultado não deve ser nulo");
        assertEquals(pagedModel, result, "Deve retornar o PagedModel do assembler");

        verify(repository, times(1)).findTreinamentoConluded(startDate, endDate, pageable);
        verify(assembler, times(1)).toModel(any(Page.class), any(Link.class));
    }

    @Test
    void findTreinamentosByFuncionario() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("dataAgendamento").ascending());
        List<Treinamento> treinamentos = List.of(
                mockTreinamento.mockEntity(1),
                mockTreinamento.mockEntity(2)
        );
        Page<Treinamento> page = new PageImpl<>(treinamentos, pageable, treinamentos.size());
        when(repository.findTreinamentosByFuncionario(1L, pageable)).thenReturn(page);

        PagedModel<EntityModel<TreinamentoDTO>> pagedModel = mock(PagedModel.class);
        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(pagedModel);

        var result = service.findTreinamentosByFuncionario(1L, pageable);

        assertNotNull(result, "Resultado não deve ser nulo");
        assertEquals(pagedModel, result, "Deve retornar o PagedModel do assembler");

        verify(repository, times(1)).findTreinamentosByFuncionario(1L, pageable);
        verify(assembler, times(1)).toModel(any(Page.class), any(Link.class));
    }

    @Test
    void create() {
        Treinamento treinamento = mockTreinamento.mockEntity(1);
        Long funcionarioId = treinamento.getFuncionario().getId();
        Long cursoId = treinamento.getCurso().getId();

        when(funcionarioRepository.findById(funcionarioId)).thenReturn(Optional.of(treinamento.getFuncionario()));
        when(cursoRepository.findById(cursoId)).thenReturn(Optional.of(treinamento.getCurso()));
        when(repository.save(any(Treinamento.class))).thenReturn(treinamento);

        var result = service.create(treinamento);

        assertNotNull(result, "Resultado não deve ser nulo");
        assertEquals(1L, result.getId(), "ID deve ser 1");

        verify(funcionarioRepository, times(1)).findById(funcionarioId);
        verify(cursoRepository, times(1)).findById(cursoId);
        verify(repository, times(2)).save(any(Treinamento.class));
    }

    @Test
    void createWithNull() {
        assertThrows(RequiredObjectIsNullException.class, () -> service.create(null),
                "Deve lançar exceção para treinamento nulo");

        verify(funcionarioRepository, times(0)).findById(anyLong());
        verify(cursoRepository, times(0)).findById(anyLong());
    }

    @Test
    void createFuncionarioNotFound() {
        Treinamento treinamento = mockTreinamento.mockEntity(1);
        treinamento.getFuncionario().setId(999L);

        when(funcionarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(treinamento),
                "Deve lançar ResourceNotFoundException quando funcionário não existe");

        verify(funcionarioRepository, times(1)).findById(999L);
        verify(cursoRepository, times(0)).findById(anyLong());
        verify(repository, times(0)).save(any(Treinamento.class));
    }

    @Test
    void createCursoNotFound() {
        Treinamento treinamento = mockTreinamento.mockEntity(1);
        Long funcionarioId = treinamento.getFuncionario().getId();
        treinamento.getCurso().setId(999L);

        when(funcionarioRepository.findById(funcionarioId)).thenReturn(Optional.of(treinamento.getFuncionario()));
        when(cursoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(treinamento),
                "Deve lançar ResourceNotFoundException quando curso não existe");

        verify(funcionarioRepository, times(1)).findById(funcionarioId);
        verify(cursoRepository, times(1)).findById(999L);
        verify(repository, times(0)).save(any(Treinamento.class));
    }

    @Test
    void update() {
        Funcionario funcionario = mock(Funcionario.class);
        funcionario.setId(1L);
        Curso curso = mock(Curso.class);
        curso.setId(1L);
        curso.setValidadeMeses(12);

        Treinamento treinamento = mockTreinamento.mockEntity(1);
        treinamento.setFuncionario(funcionario);
        treinamento.setCurso(curso);

        TreinamentoDTO dto = new TreinamentoDTO();
        dto.setId(1L);
        dto.setDataAgendamento(LocalDate.now());
        dto.setDataConcluido(LocalDate.now());
        dto.setDataVencimento(LocalDate.now().plusMonths(12));
        dto.setInstrutor("João Silva");
        dto.setStatus(TreinamentoStatus.VALIDO);
        dto.setFuncionarioId(1L);
        dto.setCursoId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(treinamento));
        when(repository.save(any(Treinamento.class))).thenReturn(treinamento);

        var result = service.update(dto);

        assertNotNull(result, "Resultado não deve ser nulo");
        assertEquals(1L, result.getId(), "ID deve ser 1");
        assertEquals("João Silva", result.getInstrutor(), "Instrutor deve ser atualizado");
        assertFalse(result.getLinks().isEmpty(), "Links HATEOAS não devem estar vazios");
        assertEquals(5, result.getLinks().stream().count(), "Deve conter exatamente 5 links HATEOAS");

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Treinamento.class));
    }

    @Test
    void updateWithNull() {
        assertThrows(RequiredObjectIsNullException.class, () -> service.update(null),
                "Deve lançar exceção para DTO nulo");

        verify(repository, times(0)).findById(anyLong());
    }

    @Test
    void updateNotFound() {
        TreinamentoDTO dto = new TreinamentoDTO();
        dto.setId(999L);

        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(dto),
                "Deve lançar ResourceNotFoundException quando treinamento não existe");

        verify(repository, times(1)).findById(999L);
        verify(repository, times(0)).save(any(Treinamento.class));
    }

    @Test
    void delete() {
        Treinamento treinamento = mockTreinamento.mockEntity(1);
        when(repository.findById(1L)).thenReturn(Optional.of(treinamento));

        assertDoesNotThrow(() -> service.delete(1L),
                "Não deve lançar exceção ao deletar treinamento existente");

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).delete(treinamento);
    }

    @Test
    void deleteNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(999L),
                "Deve lançar ResourceNotFoundException quando treinamento não existe");

        verify(repository, times(1)).findById(999L);
        verify(repository, times(0)).delete(any(Treinamento.class));
    }

    @Test
    void exportPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("dataAgendamento").ascending());
        List<Treinamento> treinamentos = List.of(mockTreinamento.mockEntity(1));
        Page<Treinamento> page = new PageImpl<>(treinamentos, pageable, 1);

        when(repository.findAll(pageable)).thenReturn(page);

        FileExporter exporter = mock(FileExporter.class);
        Resource resource = mock(Resource.class);
        when(exporterFactory.getExporter("application/pdf")).thenReturn(exporter);
        when(exporter.exportTreinamentos(any(List.class))).thenReturn(resource);

        var result = assertDoesNotThrow(() -> service.exportPage(pageable, "application/pdf"),
                "Não deve lançar exceção ao exportar PDF");

        assertNotNull(result, "Resource não deve ser nulo");

        verify(repository, times(1)).findAll(pageable);
        verify(exporterFactory, times(1)).getExporter("application/pdf");
        verify(exporter, times(1)).exportTreinamentos(any(List.class));
    }

    @Test
    void exportPageWithInvalidFormat() throws Exception {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("dataAgendamento").ascending());
        List<Treinamento> treinamentos = List.of(mockTreinamento.mockEntity(1));
        Page<Treinamento> page = new PageImpl<>(treinamentos, pageable, 1);

        when(repository.findAll(pageable)).thenReturn(page);
        when(exporterFactory.getExporter("text/invalid")).thenThrow(new RuntimeException("Formato inválido"));

        assertThrows(RuntimeException.class, () -> service.exportPage(pageable, "text/invalid"),
                "Deve lançar RuntimeException para formato inválido");

        verify(repository, times(1)).findAll(pageable);
        verify(exporterFactory, times(1)).getExporter("text/invalid");
    }

    @Test
    void atualizarStatusTreinamentos() {
        Treinamento treinamentoValido = mockTreinamento.mockEntity(1);
        treinamentoValido.setDataVencimento(LocalDate.now().plusMonths(6));

        Treinamento treinamentoProximo = mockTreinamento.mockEntity(2);
        treinamentoProximo.setDataVencimento(LocalDate.now().plusDays(30));

        Treinamento treinamentoVencido = mockTreinamento.mockEntity(3);
        treinamentoVencido.setDataVencimento(LocalDate.now().minusDays(1));

        List<Treinamento> treinamentos = List.of(treinamentoValido, treinamentoProximo, treinamentoVencido);
        when(repository.findAll()).thenReturn(treinamentos);

        assertDoesNotThrow(() -> service.atualizarStatusTreinamentos(),
                "Não deve lançar exceção ao atualizar status");

        verify(repository, times(1)).findAll();
        verify(repository, times(1)).saveAll(treinamentos);

        assertEquals(TreinamentoStatus.VALIDO, treinamentoValido.getStatus(),
                "Treinamento com vencimento em 6 meses deve ser VALIDO");
        assertEquals(TreinamentoStatus.VENCIMENTO_PROXIMO, treinamentoProximo.getStatus(),
                "Treinamento com vencimento em 30 dias deve ser VENCIMENTO_PROXIMO");
        assertEquals(TreinamentoStatus.VENCIDO, treinamentoVencido.getStatus(),
                "Treinamento com vencimento ontem deve ser VENCIDO");
    }
}
