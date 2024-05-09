package com.beyondrest.SegreteriaVirtualeREST.lezione;
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
@Tag(name = "Lezione", description = "APIs related to Lezione operations")
public class LezioneController {
    private final LezioneRepository repository;
    private final InsegnamentoRepository insegnamentoRepository;

    LezioneController(LezioneRepository repository, InsegnamentoRepository insegnamentoRepository) {
        this.repository = repository;
        this.insegnamentoRepository = insegnamentoRepository;
    }

    @Operation(summary = "Get all lezioni for an insegnamento", description = "Get all lezioni associated with a specific insegnamento")
    @GetMapping("/insegnamenti/{id}/lezioni")
    public CollectionModel<EntityModel<Lezione>> all(@PathVariable @Parameter(description = "ID of the insegnamento", example = "1") Long id) {
        var insegnamento = insegnamentoRepository.findById(id)
                .orElseThrow(() -> new InsegnamentoController.InsegnamentoNotFoundException(id));
        var lezioni = insegnamento.getLezioni().stream()
                .map(lezione -> EntityModel.of(lezione,
                        linkTo(methodOn(LezioneController.class).one(id, lezione.getId())).withSelfRel()))
                .toList();
        return CollectionModel.of(lezioni, linkTo(methodOn(LezioneController.class).all(id)).withSelfRel());
    }

    @Operation(summary = "Get a single lezione", description = "Get a single lezione by its ID associated with a specific insegnamento")
    @GetMapping("/insegnamenti/{id_insegnamento}/lezioni/{id}")
    public EntityModel<Lezione> one(@PathVariable @Parameter(description = "ID of the insegnamento", example = "1") Long id_insegnamento,
                                    @PathVariable @Parameter(description = "ID of the lezione", example = "1") Long id) {
        var lezione = repository.findById(id)
                .orElseThrow(() -> new LezioneNotFoundException(id));
        return EntityModel.of(lezione,
                linkTo(methodOn(LezioneController.class).one(id_insegnamento, id)).withSelfRel(),
                linkTo(methodOn(LezioneController.class).all(id_insegnamento)).withRel("lezioni"));
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Could not find lezione")
    public static class LezioneNotFoundException extends RuntimeException {
        public LezioneNotFoundException(Long id) {
            super("Could not find lezione " + id);
        }
    }

    @ExceptionHandler(LezioneNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleLezioneNotFoundException(LezioneNotFoundException e) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
