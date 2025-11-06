package com.example.projetoRestSpringBoot.controller.docs;

import com.example.projetoRestSpringBoot.data.dto.BookDTO;
import com.example.projetoRestSpringBoot.data.dto.PersonDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface BookControllerDocs {
    @Operation(summary = "Find all books",
            description = "Finding all books",
            tags = {"Book"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(schema = @Schema(implementation = BookDTO.class))
                                    )

                            }),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
            })
    ResponseEntity<PagedModel<EntityModel<BookDTO>>>findAll(
    @RequestParam(value = "page", defaultValue = "0") int page,
    @RequestParam(value = "size", defaultValue = "12") int size,
    @RequestParam(value = "direction", defaultValue = "asc") String direction
    );

    @Operation(summary = "Find a specific book",
            description = "Finding a specific book by Id",
            tags = {"Book"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content =
                            @Content(schema = @Schema(implementation = BookDTO.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
            })
    BookDTO findById(@PathVariable("id") long id);

    @Operation(summary = "Create a new book",
            description = "Creating a new book",
            tags = {"Book"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content =
                            @Content(schema = @Schema(implementation = BookDTO.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
            })
    BookDTO create(@RequestBody BookDTO book);

    @Operation(summary = "Update a specific books",
            description = "Updating a specific books by Id",
            tags = {"Book"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content =
                            @Content(schema = @Schema(implementation = BookDTO.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
            })
    BookDTO update(@RequestBody BookDTO book);


    @Operation(summary = "Delete a specific books",
            description = "Deleting a specific books by Id",
            tags = {"Book"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content =
                            @Content(schema = @Schema(implementation = BookDTO.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
            })
    ResponseEntity<?> delete(@PathVariable("id") long id);
}
