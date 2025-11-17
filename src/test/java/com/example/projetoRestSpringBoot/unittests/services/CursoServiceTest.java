package com.example.projetoRestSpringBoot.unittests.services;

import com.example.projetoRestSpringBoot.enums.CursoOrigem;
import com.example.projetoRestSpringBoot.enums.CursoTipoObrigatoriedade;
import com.example.projetoRestSpringBoot.file.exporter.contract.FileExporter;
import com.example.projetoRestSpringBoot.file.exporter.factory.FileExporterFactory;
import com.example.projetoRestSpringBoot.file.importer.contract.FileImporter;
import com.example.projetoRestSpringBoot.file.importer.factory.FileImporterFactory;
import com.example.projetoRestSpringBoot.model.Curso;
import com.example.projetoRestSpringBoot.repository.CursoRepository;
import com.example.projetoRestSpringBoot.service.CursoService;
import com.example.projetoRestSpringBoot.unittests.mocks.MockCurso;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import com.example.projetoRestSpringBoot.dto.CursoDTO;
import com.example.projetoRestSpringBoot.exception.BadRequestException;
import com.example.projetoRestSpringBoot.exception.RequiredObjectIsNullException;
import com.example.projetoRestSpringBoot.exception.ResourceNotFoundException;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

    MockCurso mockCurso;

    @InjectMocks
    private CursoService service;

    @Mock
    CursoRepository repository;

    @Mock
    PagedResourcesAssembler<CursoDTO> assembler;

    @Mock
    FileExporterFactory exporterFactory;

    @Mock
    FileImporterFactory importerFactory;

    @BeforeEach
    void setUp() {
        mockCurso = new MockCurso();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById() {
        Curso curso = mockCurso.mockEntity(1);
        when(repository.findById(1L)).thenReturn(Optional.of(curso));

        var result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(curso.getNome(), result.getNome());
        assertEquals(curso.getDescricao(), result.getDescricao());
        assertEquals(curso.getCargaHoraria(), result.getCargaHoraria());
        assertEquals(curso.getValidadeMeses(), result.getValidadeMeses());
        assertEquals(curso.getOrigemCurso(), result.getOrigemCurso());
        assertEquals(curso.getTipoObrigatoriedade(), result.getTipoObrigatoriedade());

        assertFalse(result.getLinks().isEmpty());

        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getType().equals("GET")));

        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getType().equals("DELETE")));


        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getType().equals("PUT")));

    }

    @Test
    void findByIdNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(999L));
    }

    @Test
    void findAll() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("nome").ascending());
        List<Curso> cursos = List.of(
                mockCurso.mockEntity(1),
                mockCurso.mockEntity(2),
                mockCurso.mockEntity(3)
        );
        Page<Curso> page = new PageImpl<>(cursos, pageable, cursos.size());
        when(repository.findAll(pageable)).thenReturn(page);

        PagedModel<EntityModel<CursoDTO>> pagedModel = mock(PagedModel.class);
        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(pagedModel);

        var result = service.findAll(pageable);

        assertNotNull(result);
    }

    @Test
    void findByName() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("nome").ascending());
        Curso curso = mockCurso.mockEntity(1);
        Page<Curso> page = new PageImpl<>(List.of(curso), pageable, 1);
        when(repository.findCursoByName("Java", pageable)).thenReturn(page);

        PagedModel<EntityModel<CursoDTO>> pagedModel = mock(PagedModel.class);
        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(pagedModel);

        var result = service.findByName("Java", pageable);

        assertNotNull(result);
    }

    @Test
    void create() {
        Curso curso = mockCurso.mockEntity(1);
        when(repository.save(any(Curso.class))).thenReturn(curso);

        var result = service.create(curso);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(curso.getNome(), result.getNome());
    }

    @Test
    void createWithNull() {
        assertThrows(RequiredObjectIsNullException.class, () -> service.create(null));
    }

    @Test
    void update() {
        Curso curso = mockCurso.mockEntity(1);
        CursoDTO dto = new CursoDTO();
        dto.setId(1L);
        dto.setNome("Java Avançado");
        dto.setDescricao("Curso de Java com padrões avançados");
        dto.setCargaHoraria(40);
        dto.setValidadeMeses(24);
        dto.setOrigemCurso(CursoOrigem.valueOf("INTERNO"));
        dto.setTipoObrigatoriedade(CursoTipoObrigatoriedade.valueOf("OBRIGATORIO"));

        when(repository.findById(1L)).thenReturn(Optional.of(curso));
        when(repository.save(any(Curso.class))).thenReturn(curso);

        var result = service.update(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertFalse(result.getLinks().isEmpty());
    }

    @Test
    void updateWithNull() {
        assertThrows(RequiredObjectIsNullException.class, () -> service.update(null));
    }

    @Test
    void updateNotFound() {
        CursoDTO dto = new CursoDTO();
        dto.setId(999L);
        dto.setNome("Curso Teste"); // ← adicionar campo obrigatório

        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(dto));
    }

    @Test
    void delete() {
        Curso curso = mockCurso.mockEntity(1);
        when(repository.findById(1L)).thenReturn(Optional.of(curso));

        assertDoesNotThrow(() -> service.delete(1L));
    }

    @Test
    void deleteNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(999L));
    }

    @Test
    void exportPage() throws Exception {
        List<Curso> cursos = List.of(mockCurso.mockEntity(1));

        when(repository.findAll()).thenReturn(cursos);

        FileExporter exporter = mock(FileExporter.class);
        Resource resource = mock(Resource.class);
        when(exporterFactory.getExporter("application/pdf")).thenReturn(exporter);
        when(exporter.exportCursos(any(List.class))).thenReturn(resource);

        var result = service.exportPage("application/pdf");

        assertNotNull(result);
    }

    @Test
    void importarArquivo() throws Exception {
        Curso curso = mockCurso.mockEntity(1);
        when(repository.save(any(Curso.class))).thenReturn(curso);

        FileImporter importer = mock(FileImporter.class);
        CursoDTO dto = new CursoDTO();
        dto.setId(1L);
        dto.setNome("Curso Test 1");
        when(importer.importarCursos(any(InputStream.class))).thenReturn(List.of(dto));
        when(importerFactory.getImporter("cursos.xlsx")).thenReturn(importer);

        MultipartFile file = new MockMultipartFile(
                "file",
                "cursos.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "conteúdo do arquivo".getBytes()
        );

        var result = assertDoesNotThrow(() -> service.importarArquivo(file));
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void importarArquivoEmpty() {
        MultipartFile file = new MockMultipartFile("file", "", "text/plain", new byte[0]);

        assertThrows(BadRequestException.class, () -> service.importarArquivo(file));
    }

    @Test
    void importarArquivoWithoutFileName() {
        MultipartFile file = new MockMultipartFile("file", "", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "conteúdo".getBytes());

        assertThrows(BadRequestException.class, () -> service.importarArquivo(file));
    }
}
