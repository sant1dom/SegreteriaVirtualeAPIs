package com.beyondrest.SegreteriaVirtualeREST.pianodistudi;

import com.beyondrest.SegreteriaVirtualeREST.ErrorResponse;
import com.beyondrest.SegreteriaVirtualeREST.corso.CorsoController;
import com.beyondrest.SegreteriaVirtualeREST.corso.CorsoRepository;
import com.beyondrest.SegreteriaVirtualeREST.insegnamento.InsegnamentoController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "Piano di Studi", description = "APIs related to Piano di Studi operations")
public class PianoDiStudiController {
    private final CorsoRepository repository;

    PianoDiStudiController(CorsoRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "Get Piano di Studi for a specific curriculum and year")
    @GetMapping("/corsi/{id}/curricula/{curriculaId}/piano/{anno}")
    public EntityModel<PianoDiStudi> getPianoDiStudi(@PathVariable @Parameter(description = "ID of the corso", example = "1") Long id,
                                                     @PathVariable @Parameter(description = "ID of the curriculum", example = "1") Long curriculaId,
                                                     @PathVariable @Parameter(description = "Year of the piano di studi", example = "2023") String anno) {
        var piano = repository.findById(id)
                .orElseThrow(() -> new CorsoController.CorsoNotFoundException(id))
                .getCurricula()
                .stream()
                .filter(curricula -> curricula.getId().equals(curriculaId) && curricula.getPianiDiStudi().stream().anyMatch(pianoDiStudi -> pianoDiStudi.getAnno().equals(anno)))
                .findFirst()
                .orElseThrow(() -> new CorsoController.CurriculumNotFoundException(curriculaId))
                .getPianiDiStudi()
                .stream()
                .filter(pianoDiStudi -> pianoDiStudi.getAnno().equals(anno))
                .map(pianoDiStudi -> EntityModel.of(pianoDiStudi,
                        linkTo(methodOn(PianoDiStudiController.class).getPianoDiStudi(id, curriculaId, anno)).withSelfRel(),
                        linkTo(methodOn(CorsoController.class).getSingleCurriculum(curriculaId, id)).withRel("curricula")))
                .findFirst()
                .orElseThrow(() -> new PianoNotFoundException(id));
        Objects.requireNonNull(piano.getContent()).getInsegnamenti().forEach(insegnamento -> piano.add(linkTo(methodOn(InsegnamentoController.class).one(insegnamento.getId())).withRel("insegnamenti")));
        return piano;
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Could not find piano")
    public static class PianoNotFoundException extends RuntimeException {
        public PianoNotFoundException(Long id) {
            super("Could not find piano " + id);
        }
    }

    @ExceptionHandler(PianoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse handlePianoNotFound(PianoNotFoundException ex) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }
}
