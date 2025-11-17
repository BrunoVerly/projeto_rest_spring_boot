package com.example.projetoRestSpringBoot.controller.docs;

import com.example.projetoRestSpringBoot.dto.CursoDTO;
import com.example.projetoRestSpringBoot.dto.FuncionarioDTO;
import com.example.projetoRestSpringBoot.file.exporter.MediaTypes;
import com.example.projetoRestSpringBoot.model.Curso;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Curso", description = "Endpoints para gerenciamento de curso")
public interface CursoControllerDocs {
    @Operation(summary = "Buscar todos os cursos",
            description = "Endpoint para retornar uma lista paginada com HATEOAS, de todos os cursos",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(schema = @Schema(implementation = CursoDTO.class))
                                    )

                            }),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
            })
    ResponseEntity<PagedModel<EntityModel<CursoDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    );

    @Operation(summary = "Buscar cursos por nome",
            description = "Endpoint para retornar uma lista paginada com HATEOAS, de cursos filtrados pelo nome",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(schema = @Schema(implementation = FuncionarioDTO.class))
                                    )

                            }),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
            })
    ResponseEntity<PagedModel<EntityModel<CursoDTO>>> findByName(
            @PathVariable("nome") String nome,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    );



    @Operation(summary = "Buscar um curso pelo id",
            description = "Endpoint para retornar um curso especifico filtrado pelo id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content =
                            @Content(schema = @Schema(implementation = CursoDTO.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
            })
    CursoDTO findById(@PathVariable("id") long id);

    @Operation(summary = "Cadastrar um novo curso",
            description = "Endpoint para cadastrar um novo curso",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content =
                            @Content(schema = @Schema(implementation = CursoDTO.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
            })
    Curso create(@RequestBody Curso curso);

    @Operation(summary = "Atualizar um curso",
            description = "Endpoint para atualizar um curso especificado filtrado pelo id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content =
                            @Content(schema = @Schema(implementation = Curso.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
            })
    CursoDTO update(@RequestBody CursoDTO curso);

    @Operation(summary = "Apagar um curso",
            description = "Endpoint para apagar um curso especifico filtrado pelo id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content =
                            @Content(schema = @Schema(implementation = CursoDTO.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
            })
    ResponseEntity<?> delete(@PathVariable("id") long id);

    @Operation(summary = "Criar vários cursos por meio de upload de arquivo",
            description = "Endpoint para criar vários cursos por meio de upload de arquivo em formatos CSV ou XLSX",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = CursoDTO.class)
                                    )

                            }),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
            })
    List<CursoDTO> importarCursos(MultipartFile file);

    @Operation(summary = "Exportar cursos",
            description = "Endpoint para exportar o banco de cursos em arquivos nos formatos CSV ou XLSX",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(mediaType = MediaTypes.APPLICATION_XLSX_VALUE),
                                    @Content(mediaType = MediaTypes.APPLICATION_TEXT_CSV_VALUE)

                            }),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
            })
    ResponseEntity<Resource> exportPage(
            HttpServletRequest request

    );

}
