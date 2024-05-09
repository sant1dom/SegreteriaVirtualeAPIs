package com.beyondrest.SegreteriaVirtualeREST.docente;

import com.beyondrest.SegreteriaVirtualeREST.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "Docente", description = "APIs related to Docente operations")
public class DocenteController {
    private final DocenteRepository repository;

    DocenteController(DocenteRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "Get all docenti", description = "Get all docenti along with their details")
    @GetMapping("/docenti")
    public CollectionModel<EntityModel<Docente>> all() {
        var docenti = repository.findAll().stream()
                .map(docente -> EntityModel.of(docente,
                        linkTo(methodOn(DocenteController.class).one(docente.getId())).withSelfRel(),
                        linkTo(methodOn(DocenteController.class).all()).withRel("docenti")))
                .toList();
        return CollectionModel.of(docenti, linkTo(methodOn(DocenteController.class).all()).withSelfRel());
    }

    @Operation(summary = "Get a single docente", description = "Get a single docente by its ID along with its details")
    @GetMapping("/docenti/{id}")
    public EntityModel<Docente> one(@PathVariable @Parameter(description = "ID of the docente", example = "1") Long id) {
        var docente = repository.findById(id)
                .orElseThrow(() -> new DocenteNotFoundException(id));
        return EntityModel.of(docente,
                linkTo(methodOn(DocenteController.class).one(id)).withSelfRel(),
                linkTo(methodOn(DocenteController.class).all()).withRel("docenti"));
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Could not find docente")
    public static class DocenteNotFoundException extends RuntimeException {
        public DocenteNotFoundException(Long id) {
            super("Could not find docente " + id);
        }
    }

    @ExceptionHandler(DocenteNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleDocenteNotFoundException(DocenteNotFoundException e) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
