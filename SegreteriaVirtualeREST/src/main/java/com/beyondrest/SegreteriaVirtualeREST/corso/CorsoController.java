package com.beyondrest.SegreteriaVirtualeREST.corso;

import com.beyondrest.SegreteriaVirtualeREST.ErrorResponse;
import com.beyondrest.SegreteriaVirtualeREST.curriculum.Curriculum;
import com.beyondrest.SegreteriaVirtualeREST.pianodistudi.PianoDiStudiController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "Corso", description = "APIs related to Corso operations")
public class CorsoController {
    private final CorsoRepository repository;

    CorsoController(CorsoRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "Get all corsi", description = "Get all corsi along with their curricula")
    @GetMapping("/corsi")
    CollectionModel<EntityModel<Corso>> all() {
        var corsi = repository.findAll().stream()
                .map(corso -> EntityModel.of(corso,
                        linkTo(methodOn(CorsoController.class).one(corso.getId())).withSelfRel(),
                        linkTo(methodOn(CorsoController.class).getCurricula(corso.getId())).withRel("curricula")))
                .toList();
        return CollectionModel.of(corsi, linkTo(methodOn(CorsoController.class).all()).withSelfRel());
    }

    @Operation(summary = "Create a new corso", description = "Create a new corso and return it along with its curricula")
    @PostMapping("/corsi")
    EntityModel<Corso> newCorso(@RequestBody Corso newCorso) {
        var corso = repository.save(newCorso);
        return EntityModel.of(corso,
                linkTo(methodOn(CorsoController.class).one(corso.getId())).withSelfRel(),
                linkTo(methodOn(CorsoController.class).getCurricula(corso.getId())).withRel("curricula"));
    }

    @Operation(summary = "Get a single corso", description = "Get a single corso by its ID along with its curricula")
    @GetMapping("/corsi/{id}")
    EntityModel<Corso> one(@PathVariable @Parameter(description = "ID of the corso", example = "1") Long id) {
        var corso = repository.findById(id)
                .orElseThrow(() -> new CorsoNotFoundException(id));
        return EntityModel.of(corso,
                linkTo(methodOn(CorsoController.class).one(id)).withSelfRel(),
                linkTo(methodOn(CorsoController.class).getCurricula(id)).withRel("curricula"));
    }

    @Operation(summary = "Get curricula for a corso", description = "Get all curricula associated with a corso")
    @GetMapping("/corsi/{id}/curricula")
    CollectionModel<EntityModel<Curriculum>> getCurricula(@PathVariable @Parameter(description = "ID of the corso", example = "1") Long id) {
        var curricula = repository.findById(id)
                .orElseThrow(() -> new CorsoNotFoundException(id))
                .getCurricula()
                .stream()
                .map(curriculum -> EntityModel.of(curriculum,
                        linkTo(methodOn(CorsoController.class).getSingleCurriculum(curriculum.getId(), id)).withSelfRel(),
                        linkTo(methodOn(CorsoController.class).getCurricula(id)).withRel("curricula")))
                .toList();
        return CollectionModel.of(curricula, linkTo(methodOn(CorsoController.class).getCurricula(id)).withSelfRel());
    }

    @Operation(summary = "Get a single curriculum for a corso", description = "Get a single curriculum for a corso by their respective IDs")
    @GetMapping("/corsi/{id}/curricula/{curriculumId}")
    public EntityModel<Curriculum> getSingleCurriculum(
            @PathVariable @Parameter(description = "ID of the corso", example = "1") Long id,
            @PathVariable @Parameter(description = "ID of the curriculum", example = "1") Long curriculumId) {
        return repository.findById(id)
                .orElseThrow(() -> new CorsoNotFoundException(id))
                .getCurricula()
                .stream()
                .filter(curricula -> curricula.getId().equals(curriculumId))
                .findFirst()
                .map(curricula -> EntityModel.of(curricula,
                        linkTo(methodOn(CorsoController.class).getCurricula(id)).withSelfRel()))
                .map(entityModel -> {
                    var curricula = entityModel.getContent();
                    Objects.requireNonNull(curricula).getPianiDiStudi().forEach(pianoDiStudi -> entityModel.add(linkTo(methodOn(PianoDiStudiController.class).getPianoDiStudi(id, curriculumId, pianoDiStudi.getAnno())).withRel("piani_di_studio")));
                    return entityModel;
                })
                .orElseThrow(() -> new CurriculumNotFoundException(curriculumId));
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Could not find corso")
    public static class CorsoNotFoundException extends RuntimeException {
        public CorsoNotFoundException(Long id) {
            super("Could not find corso " + id);
        }
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Could not find curriculum")
    public static class CurriculumNotFoundException extends RuntimeException {
        public CurriculumNotFoundException(Long id) {
            super("Could not find curriculum " + id);
        }
    }

    @ExceptionHandler(CorsoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse handleCorsoNotFound(CorsoNotFoundException ex) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CurriculumNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse handleCurriculumNotFound(CurriculumNotFoundException ex) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }
}
