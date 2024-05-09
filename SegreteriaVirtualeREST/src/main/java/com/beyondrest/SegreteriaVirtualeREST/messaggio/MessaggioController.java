package com.beyondrest.SegreteriaVirtualeREST.messaggio;

import com.beyondrest.SegreteriaVirtualeREST.ErrorResponse;
import com.beyondrest.SegreteriaVirtualeREST.insegnamento.InsegnamentoController;
import com.beyondrest.SegreteriaVirtualeREST.insegnamento.InsegnamentoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "Messaggio", description = "APIs related to Messaggio operations")
public class MessaggioController {
    private final MessaggioRepository repository;
    private final InsegnamentoRepository insegnamentoRepository;

    MessaggioController(MessaggioRepository repository, InsegnamentoRepository insegnamentoRepository) {
        this.repository = repository;
        this.insegnamentoRepository = insegnamentoRepository;
    }

    @Operation(summary = "Get all messaggi on the bacheca of an insegnamento", description = "Get all messaggi on the bacheca of a specific insegnamento")
    @GetMapping("/insegnamenti/{insegnamento_id}/bacheca")
    public CollectionModel<EntityModel<Messaggio>> all(@PathVariable @Parameter(description = "ID of the insegnamento", example = "1") Long insegnamento_id) {
        var messaggi = insegnamentoRepository.findById(insegnamento_id).stream()
                .flatMap(insegnamento -> insegnamento.getBacheca().stream())
                .map(messaggio -> EntityModel.of(messaggio, linkTo(methodOn(MessaggioController.class).one(insegnamento_id, messaggio.getId())).withSelfRel()))
                .toList();
        return CollectionModel.of(messaggi, linkTo(methodOn(MessaggioController.class).all(insegnamento_id)).withSelfRel());
    }

    @Operation(summary = "Get a single messaggio from the bacheca of an insegnamento", description = "Get a single messaggio from the bacheca of a specific insegnamento by its ID")
    @GetMapping("/insegnamenti/{insegnamento_id}/bacheca/{id}")
    public EntityModel<Messaggio> one(@PathVariable @Parameter(description = "ID of the insegnamento", example = "1") Long insegnamento_id,
                                      @PathVariable @Parameter(description = "ID of the messaggio", example = "1") Long id) {
        var insegnamento = insegnamentoRepository.findById(insegnamento_id)
                .orElseThrow(() -> new InsegnamentoController.InsegnamentoNotFoundException(insegnamento_id));
        var messaggio = insegnamento.getBacheca().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new MessaggioNotFoundException(id));
        return EntityModel.of(messaggio,
                linkTo(methodOn(MessaggioController.class).one(insegnamento_id, id)).withSelfRel(),
                linkTo(methodOn(MessaggioController.class).all(insegnamento_id)).withRel("bacheca"));
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Could not find messaggio")
    public static class MessaggioNotFoundException extends RuntimeException {
        public MessaggioNotFoundException(Long id) {
            super("Could not find messaggio " + id);
        }
    }

    @ExceptionHandler(MessaggioNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleMessaggioNotFound(MessaggioNotFoundException ex) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }
}
