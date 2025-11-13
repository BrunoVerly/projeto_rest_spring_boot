package com.example.projetoRestSpringBoot.unittests.services;

import com.example.projetoRestSpringBoot.enums.FuncionarioSituacao;
import com.example.projetoRestSpringBoot.file.exporter.contract.FileExporter;
import com.example.projetoRestSpringBoot.file.exporter.factory.FileExporterFactory;
import com.example.projetoRestSpringBoot.model.Funcionario;
import com.example.projetoRestSpringBoot.repository.FuncionarioRepository;
import com.example.projetoRestSpringBoot.service.FuncionarioService;
import com.example.projetoRestSpringBoot.unittests.mocks.MockFuncionario;
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
import com.example.projetoRestSpringBoot.dto.FuncionarioDTO;
import com.example.projetoRestSpringBoot.exception.RequiredObjectIsNullException;
import com.example.projetoRestSpringBoot.exception.ResourceNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {

    MockFuncionario mockFuncionario;

    @InjectMocks
    private FuncionarioService service;

    @Mock
    FuncionarioRepository repository;

    @Mock
    PagedResourcesAssembler<FuncionarioDTO> assembler;

    @Mock
    FileExporterFactory exporterFactory;

    @BeforeEach
    void setUp() {
        mockFuncionario = new MockFuncionario();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById() {
        Funcionario funcionario = mockFuncionario.mockEntity(1);
        Long funcionarioId = funcionario.getId();

        when(repository.findById(funcionarioId)).thenReturn(Optional.of(funcionario));

        var result = service.findById(funcionarioId);

        assertNotNull(result, "Resultado não deve ser nulo");
        assertEquals(funcionarioId, result.getId(), "ID deve corresponder");
        assertEquals(funcionario.getNome(), result.getNome(), "Nome deve corresponder");
        assertEquals(funcionario.getMatricula(), result.getMatricula(), "Matrícula deve corresponder");
        assertEquals(funcionario.getCargo(), result.getCargo(), "Cargo deve corresponder");
        assertEquals(funcionario.getDepartamento(), result.getDepartamento(), "Departamento deve corresponder");

        assertFalse(result.getLinks().isEmpty(), "Links não devem estar vazios");
        assertEquals(5, result.getLinks().toList().size(), "Deve conter 5 links");


        assertTrue(result.getLinks().stream()
                        .anyMatch(link -> "self".equals(link.getRel().value()) && "GET".equals(link.getType())),
                "Deve conter link self com tipo GET");

        assertTrue(result.getLinks().stream()
                        .anyMatch(link -> "delete".equals(link.getRel().value()) && "DELETE".equals(link.getType())),
                "Deve conter link delete com tipo DELETE");

        assertTrue(result.getLinks().stream()
                        .anyMatch(link -> "update".equals(link.getRel().value()) && "PUT".equals(link.getType())),
                "Deve conter link update com tipo PUT");

        assertTrue(result.getLinks().stream()
                        .anyMatch(link -> "findAll".equals(link.getRel().value()) && "GET".equals(link.getType())),
                "Deve conter link findAll com tipo GET");

        assertTrue(result.getLinks().stream()
                        .anyMatch(link -> "situacao".equals(link.getRel().value()) && "GET".equals(link.getType())),
                "Deve conter link situacao com tipo GET");

        verify(repository, times(1)).findById(funcionarioId);
    }

    @Test
    void findByIdNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(999L),
                "Deve lançar ResourceNotFoundException quando funcionário não existe");

        verify(repository, times(1)).findById(999L);
    }

    @Test
    void findAll() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("nome").ascending());
        List<Funcionario> funcionarios = List.of(
                mockFuncionario.mockEntity(1),
                mockFuncionario.mockEntity(2)
        );
        Page<Funcionario> page = new PageImpl<>(funcionarios, pageable, funcionarios.size());

        when(repository.findAll(pageable)).thenReturn(page);

        PagedModel<EntityModel<FuncionarioDTO>> pagedModel = mock(PagedModel.class);
        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(pagedModel);

        var result = service.findAll(pageable);

        assertNotNull(result, "Resultado não deve ser nulo");
        verify(repository, times(1)).findAll(pageable);
        verify(assembler, times(1)).toModel(any(Page.class), any(Link.class));
    }

    @Test
    void findByName() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("nome").ascending());
        List<Funcionario> funcionarios = List.of(
                mockFuncionario.mockEntity(1),
                mockFuncionario.mockEntity(2)
        );
        Page<Funcionario> page = new PageImpl<>(funcionarios, pageable, funcionarios.size());

        when(repository.findFuncionarioByName("João", pageable)).thenReturn(page);

        PagedModel<EntityModel<FuncionarioDTO>> pagedModel = mock(PagedModel.class);
        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(pagedModel);

        var result = service.findByName("João", pageable);

        assertNotNull(result, "Resultado não deve ser nulo");
        verify(repository, times(1)).findFuncionarioByName("João", pageable);
        verify(assembler, times(1)).toModel(any(Page.class), any(Link.class));
    }

    @Test
    void findByMatricula() {
        Funcionario funcionario = mockFuncionario.mockEntity(1);

        when(repository.findByMatricula("MAT001")).thenReturn(Optional.of(funcionario));

        var result = service.findByMatricula("MAT001");

        assertNotNull(result, "Resultado não deve ser nulo");
        assertEquals(funcionario.getId(), result.getId(), "ID deve corresponder");
        assertEquals("MAT001", result.getMatricula(), "Matrícula deve corresponder");

        assertFalse(result.getLinks().isEmpty(), "Links não devem estar vazios");

        verify(repository, times(1)).findByMatricula("MAT001");
    }

    @Test
    void findByMatriculaNotFound() {
        when(repository.findByMatricula("MAT999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findByMatricula("MAT999"),
                "Deve lançar ResourceNotFoundException quando matrícula não existe");

        verify(repository, times(1)).findByMatricula("MAT999");
    }

    @Test
    void findBySituacao() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("nome").ascending());
        List<Funcionario> funcionarios = List.of(mockFuncionario.mockEntity(1));
        Page<Funcionario> page = new PageImpl<>(funcionarios, pageable, 1);

        FuncionarioSituacao situacao = funcionarios.get(0).getSituacao();

        when(repository.findBySituacao(eq(situacao), eq(pageable))).thenReturn(page);

        PagedModel<EntityModel<FuncionarioDTO>> pagedModel = mock(PagedModel.class);
        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(pagedModel);

        var result = service.findBySituacao(situacao, pageable);

        assertNotNull(result, "Resultado não deve ser nulo");
        verify(repository, times(1)).findBySituacao(eq(situacao), eq(pageable));
        verify(assembler, times(1)).toModel(any(Page.class), any(Link.class));
    }

    @Test
    void findFuncionarioByAddmitedDate() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("nome").ascending());
        List<Funcionario> funcionarios = List.of(mockFuncionario.mockEntity(1));
        Page<Funcionario> page = new PageImpl<>(funcionarios, pageable, 1);
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        when(repository.findFuncionarioByAddmitedDate(startDate, endDate, pageable)).thenReturn(page);

        PagedModel<EntityModel<FuncionarioDTO>> pagedModel = mock(PagedModel.class);
        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(pagedModel);

        var result = service.findFuncionarioByAddmitedDate(startDate, endDate, pageable);

        assertNotNull(result, "Resultado não deve ser nulo");
        verify(repository, times(1)).findFuncionarioByAddmitedDate(startDate, endDate, pageable);
        verify(assembler, times(1)).toModel(any(Page.class), any(Link.class));
    }

    @Test
    void create() {
        Funcionario funcionario = mockFuncionario.mockEntity(1);

        when(repository.save(any(Funcionario.class))).thenReturn(funcionario);

        var result = service.create(funcionario);

        assertNotNull(result, "Resultado não deve ser nulo");
        assertEquals(funcionario.getId(), result.getId(), "ID deve corresponder");
        assertEquals(funcionario.getNome(), result.getNome(), "Nome deve corresponder");

        verify(repository, times(2)).save(any(Funcionario.class));
    }

    @Test
    void createWithNull() {
        assertThrows(RequiredObjectIsNullException.class, () -> service.create(null),
                "Deve lançar RequiredObjectIsNullException quando funcionário é nulo");

        verify(repository, times(0)).save(any(Funcionario.class));
    }

    @Test
    void update() {
        Funcionario funcionario = mockFuncionario.mockEntity(1);
        Long funcionarioId = funcionario.getId();

        FuncionarioDTO dto = new FuncionarioDTO();
        dto.setId(funcionarioId);
        dto.setNome("João Silva Atualizado");
        dto.setMatricula("MAT001");
        dto.setCargo("Engenheiro Senior");
        dto.setDepartamento("TI");

        when(repository.findById(funcionarioId)).thenReturn(Optional.of(funcionario));
        when(repository.save(any(Funcionario.class))).thenReturn(funcionario);

        var result = service.update(dto);

        assertNotNull(result, "Resultado não deve ser nulo");
        assertEquals(funcionarioId, result.getId(), "ID deve corresponder");

        assertFalse(result.getLinks().isEmpty(), "Links não devem estar vazios");
        assertEquals(5, result.getLinks().toList().size(), "Deve conter 5 links");


        assertTrue(result.getLinks().stream()
                        .anyMatch(link -> "self".equals(link.getRel().value())),
                "Deve conter link self");

        verify(repository, times(1)).findById(funcionarioId);
        verify(repository, times(1)).save(any(Funcionario.class));
    }

    @Test
    void updateWithNull() {
        assertThrows(RequiredObjectIsNullException.class, () -> service.update(null),
                "Deve lançar RequiredObjectIsNullException quando funcionário é nulo");

        verify(repository, times(0)).findById(any());
        verify(repository, times(0)).save(any(Funcionario.class));
    }

    @Test
    void updateNotFound() {
        FuncionarioDTO dto = new FuncionarioDTO();
        dto.setId(999L);

        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(dto),
                "Deve lançar ResourceNotFoundException quando funcionário não existe");

        verify(repository, times(1)).findById(999L);
        verify(repository, times(0)).save(any(Funcionario.class));
    }

    @Test
    void delete() {
        Funcionario funcionario = mockFuncionario.mockEntity(1);
        Long funcionarioId = funcionario.getId();

        when(repository.findById(funcionarioId)).thenReturn(Optional.of(funcionario));

        assertDoesNotThrow(() -> service.delete(funcionarioId),
                "Não deve lançar exceção ao deletar funcionário existente");

        verify(repository, times(1)).findById(funcionarioId);
        verify(repository, times(1)).delete(funcionario);
    }

    @Test
    void deleteNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(999L),
                "Deve lançar ResourceNotFoundException quando funcionário não existe");

        verify(repository, times(1)).findById(999L);
        verify(repository, times(0)).delete(any(Funcionario.class));
    }

    @Test
    void exportPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("nome").ascending());
        List<Funcionario> funcionarios = List.of(mockFuncionario.mockEntity(1));
        Page<Funcionario> page = new PageImpl<>(funcionarios, pageable, 1);

        when(repository.findAll(pageable)).thenReturn(page);

        FileExporter exporter = mock(FileExporter.class);
        Resource resource = mock(Resource.class);
        when(exporterFactory.getExporter("application/pdf")).thenReturn(exporter);
        when(exporter.exportarFuncionarios(any(List.class))).thenReturn(resource);

        var result = service.exportPage(pageable, "application/pdf");

        assertNotNull(result, "Resultado não deve ser nulo");
        verify(repository, times(1)).findAll(pageable);
        verify(exporterFactory, times(1)).getExporter("application/pdf");
        verify(exporter, times(1)).exportarFuncionarios(any(List.class));
    }

    @Test
    void exportPageWithInvalidFormat() throws Exception {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("nome").ascending());
        List<Funcionario> funcionarios = List.of(mockFuncionario.mockEntity(1));
        Page<Funcionario> page = new PageImpl<>(funcionarios, pageable, 1);

        when(repository.findAll(pageable)).thenReturn(page);
        when(exporterFactory.getExporter("text/invalid")).thenThrow(new RuntimeException("Formato inválido"));

        assertThrows(RuntimeException.class, () -> service.exportPage(pageable, "text/invalid"),
                "Deve lançar RuntimeException quando formato é inválido");

        verify(repository, times(1)).findAll(pageable);
        verify(exporterFactory, times(1)).getExporter("text/invalid");
    }
}
