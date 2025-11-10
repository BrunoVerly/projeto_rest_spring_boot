package com.example.projetoRestSpringBoot.controller.docs;

import com.example.projetoRestSpringBoot.dto.CredencialDTO;
import com.example.projetoRestSpringBoot.dto.FuncionarioDTO;
import com.example.projetoRestSpringBoot.enums.CredencialStatus;
import com.example.projetoRestSpringBoot.model.Credencial;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Tag(name = "Credenciais", description = "Endpoints para gerenciamento dos credencials")
public interface CredencialControllerDocs {
    @Operation(summary = "Buscar todos os credencials",
            description = "Endpoint para retornar uma lista paginada de todos os credencials",
            tags = {"Credenciais"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(schema = @Schema(implementation = CredencialDTO.class))
                                    )

                            }),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
            })
    ResponseEntity<PagedModel<EntityModel<CredencialDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    );

    @Operation(summary = "Buscar credenciais pelo intervalo de datas de emissão",
            description = "Endpoint para retornar uma lista paginada de credenciais filtrados pelo intervalo de datas de emissão",
            tags = {"Credenciais"},
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
    ResponseEntity<PagedModel<EntityModel<CredencialDTO>>> findCredencialEmited(
            @RequestBody Map<String, String> body,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    );

    @GetMapping(value = "/buscarPorStatus/{status}", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE})
    ResponseEntity<PagedModel<EntityModel<CredencialDTO>>> findByStatus(
            @PathVariable("status") CredencialStatus status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    );

    @Operation(summary = "Buscar credenciais pelo intervalo de datas de vencimento",
            description = "Endpoint para retornar uma lista paginada de credenciais filtrados pelo intervalo de datas de vencimento",
            tags = {"Credenciais"},
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
    ResponseEntity<PagedModel<EntityModel<CredencialDTO>>> findCredencialExpiring(
            @RequestBody Map<String, String> body,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    );

    /**
    @Operation(summary = "Buscar um funcionário pelo nome",
            description = "Endpoint para retornar uma lista paginada de funcionários filtrados pelo nome",
            tags = {"Credenciais"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(schema = @Schema(implementation = CredencialDTO.class))
                                    )

                            }),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
            })
        ResponseEntity<PagedModel<EntityModel<CredencialDTO>>> findByName(
                @PathVariable("nome") String nome,
                @RequestParam(value = "page", defaultValue = "0") int page,
                @RequestParam(value = "size", defaultValue = "12") int size,
                @RequestParam(value = "direction", defaultValue = "asc") String direction
        );
    **/



    @Operation(summary = "Buscar um credencial pelo Id",
            description = "Endpoint para retornar um credencial especifico pelo Id",
            tags = {"Credenciais"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content =
                            @Content(schema = @Schema(implementation = CredencialDTO.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
            })
    CredencialDTO findById(@PathVariable("id") long id);

    @Operation(summary = "Cadastrar um novo credencial",
            description = "Endpoint para cadastrar um novo credencial",
            tags = {"Credenciais"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content =
                            @Content(schema = @Schema(implementation = CredencialDTO.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
            })
    Credencial create(@RequestBody Credencial credencial);

    @Operation(summary = "Atualizar um credencial especifico",
            description = "Endpoint para atualizar um credencial especifico pelo Id",
            tags = {"Credenciais"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content =
                            @Content(schema = @Schema(implementation = Credencial.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
            })
    CredencialDTO update(@RequestBody CredencialDTO credencial);

    @Operation(summary = "Deletar um credencial especifico",
            description = "Endpoint para deletar um credencial especifico pelo Id",
            tags = {"Credenciais"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content =
                            @Content(schema = @Schema(implementation = CredencialDTO.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
            })
    ResponseEntity<?> delete(@PathVariable("id") long id);

    /**
    @Operation(summary = "Criar vários funcionários por meio de upload de arquivo",
            description = "Endpoint para criar vários funcionários por meio de upload de arquivo em formatos CSV ou XLSX",
            tags = {"Credenciais"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = CredencialDTO.class)
                                    )

                            }),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
            })
    List <CredencialDTO> massCreation(MultipartFile file);
    **/
    /**
    @Operation(summary = "Exportar todos os credencials",
            description = "Endpoint para exportar uma lista paginada de todos os credencials em diferentes em CSV ou XLSX",
            tags = {"Credenciais"},
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
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            HttpServletRequest request

    );
            **/
}
