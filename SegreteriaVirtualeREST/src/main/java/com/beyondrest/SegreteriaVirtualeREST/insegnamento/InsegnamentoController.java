package com.beyondrest.SegreteriaVirtualeREST.insegnamento;

import com.beyondrest.SegreteriaVirtualeREST.ErrorResponse;
import com.beyondrest.SegreteriaVirtualeREST.appello.AppelloController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.beyondrest.SegreteriaVirtualeREST.docente.DocenteController;
import com.beyondrest.SegreteriaVirtualeREST.lezione.LezioneController;
import com.beyondrest.SegreteriaVirtualeREST.messaggio.MessaggioController;

import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "Insegnamento", description = "APIs related to Insegnamento operations")
public class InsegnamentoController {
    private final InsegnamentoRepository repository;

    InsegnamentoController(InsegnamentoRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "Get all insegnamenti", description = "Get all insegnamenti along with their details")
    @GetMapping("/insegnamenti")
    public CollectionModel<EntityModel<Insegnamento>> all() {
        var insegnamenti = repository.findAll().stream()
                .map(insegnamento -> EntityModel.of(insegnamento, linkTo(methodOn(InsegnamentoController.class).one(insegnamento.getId())).withSelfRel()))
                .peek(ins -> Objects.requireNonNull(ins.getContent()).getDocenti().forEach(doc -> ins.add(linkTo(methodOn(DocenteController.class).one(doc.getId())).withRel("docenti"))))
                .peek(inse -> Objects.requireNonNull(inse.getContent()).getLezioni().forEach(lezione -> inse.add(linkTo(methodOn(LezioneController.class).one(inse.getContent().getId(), lezione.getId())).withRel("lezioni"))))
                .peek(inseg -> Objects.requireNonNull(inseg.getContent()).getBacheca().forEach(messaggio -> inseg.add(linkTo(methodOn(MessaggioController.class).one(inseg.getContent().getId(), messaggio.getId())).withRel("messaggi"))))
                .peek(insegn -> Objects.requireNonNull(insegn.getContent()).getAppelli().forEach(appello -> insegn.add(linkTo(methodOn(AppelloController.class).one(insegn.getContent().getId(), appello.getId())).withRel("appelli"))))
                .toList();
        return CollectionModel.of(insegnamenti, linkTo(methodOn(InsegnamentoController.class).all()).withSelfRel());
    }

    @Operation(summary = "Get a single insegnamento", description = "Get a single insegnamento by its ID along with its details")
    @GetMapping("/insegnamenti/{id}")
    public EntityModel<Insegnamento> one(@PathVariable @Parameter(description = "ID of the insegnamento", example = "1") Long id) {
        var insegnamento = repository.findById(id).orElseThrow(() -> new InsegnamentoNotFoundException(id));
        var insegnamentoModel = EntityModel.of(insegnamento,
                linkTo(methodOn(InsegnamentoController.class).one(id)).withSelfRel());
        insegnamento.getDocenti()
                .forEach(doc -> insegnamentoModel.add(linkTo(methodOn(DocenteController.class).one(doc.getId())).withRel("docenti")));
        insegnamento.getLezioni()
                .forEach(lezione -> insegnamentoModel.add(linkTo(methodOn(LezioneController.class).one(insegnamento.getId(),
                                                                                            lezione.getId())).withRel("lezioni")));
        insegnamento.getBacheca()
                .forEach(messaggio -> insegnamentoModel.add(linkTo(methodOn(MessaggioController.class).one(insegnamento.getId(),
                                                                                            messaggio.getId())).withRel("messaggi")));
        insegnamento.getAppelli()
                .forEach(appello -> insegnamentoModel.add(linkTo(methodOn(AppelloController.class).one(insegnamento.getId(),
                                                                                            appello.getId())).withRel("appelli")));
        insegnamentoModel.add(linkTo(methodOn(InsegnamentoController.class).all()).withRel("insegnamenti"));
        return insegnamentoModel;
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Could not find insegnamento")
    public static class InsegnamentoNotFoundException extends RuntimeException {
        public InsegnamentoNotFoundException(Long id) {
            super("Could not find insegnamento " + id);
        }
    }

    @ExceptionHandler(InsegnamentoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleInsegnamentoNotFoundException(InsegnamentoNotFoundException e) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

}