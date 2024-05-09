package com.beyondrest.SegreteriaVirtualeREST.appello;

import com.beyondrest.SegreteriaVirtualeREST.ErrorResponse;
import com.beyondrest.SegreteriaVirtualeREST.insegnamento.Insegnamento;
import com.beyondrest.SegreteriaVirtualeREST.insegnamento.InsegnamentoController;
import com.beyondrest.SegreteriaVirtualeREST.insegnamento.InsegnamentoRepository;
import com.beyondrest.SegreteriaVirtualeREST.studente.StudenteRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "Appello", description = "APIs related to Appello operations")
public class AppelloController {
    InsegnamentoRepository insegnamentoRepository;
    StudenteRepository studenteRepository;
    AppelloRepository appelloRepository;

    AppelloController(InsegnamentoRepository insegnamentoRepository, StudenteRepository studenteRepository, AppelloRepository appelloRepository) {
        this.insegnamentoRepository = insegnamentoRepository;
        this.studenteRepository = studenteRepository;
        this.appelloRepository = appelloRepository;
    }

    @Operation(summary = "Get all appelli for a specific insegnamento")
    @GetMapping("/insegnamenti/{id}/appelli")
    public CollectionModel<EntityModel<Appello>> all(@PathVariable @Parameter(description = "ID of the insegnamento", example = "1") Long id) {
        var insegnamento = insegnamentoRepository.findById(id).orElseThrow(() -> new InsegnamentoController.InsegnamentoNotFoundException(id));
        var appelli = insegnamento.getAppelli().stream().map(appello -> EntityModel.of(appello, linkTo(methodOn(AppelloController.class).one(id, appello.getId())).withSelfRel())).toList();
        return CollectionModel.of(appelli, linkTo(methodOn(AppelloController.class).all(id)).withSelfRel());
    }

    @Operation(summary = "Get a specific appello for an insegnamento")
    @GetMapping("/insegnamenti/{id_insegnamento}/appelli/{id}")
    public EntityModel<Appello> one(@PathVariable @Parameter(description = "ID of the insegnamento", example = "1") Long id_insegnamento,
                                    @PathVariable @Parameter(description = "ID of the appello", example = "1") Long id) {
        var appello = insegnamentoRepository.findById(id_insegnamento).orElseThrow(() -> new InsegnamentoController.InsegnamentoNotFoundException(id_insegnamento)).getAppelli().stream().filter(a -> a.getId().equals(id)).findFirst().orElseThrow(() -> new AppelloNotFoundException(id));
        return EntityModel.of(appello, linkTo(methodOn(AppelloController.class).one(id_insegnamento, id)).withSelfRel(), linkTo(methodOn(AppelloController.class).all(id_insegnamento)).withRel("appelli"));
    }

    @PostMapping("/insegnamenti/{id}/appelli/{id_appello}")
    @Operation(summary = "Subscribe to an appello", description = "Subscribe to an appello for an insegnamento")
    @Secured("ROLE_STUDENTE")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> subscribe(@PathVariable @Parameter(description = "ID of the insegnamento", example = "1") Long id,
                                    @PathVariable @Parameter(description = "ID of the appello", example = "1") Long id_appello) {
        var insegnamento = insegnamentoRepository.findById(id).orElseThrow(() -> new InsegnamentoController.InsegnamentoNotFoundException(id));
        var appello = insegnamento.getAppelli().stream().filter(a -> a.getId().equals(id_appello)).findFirst().orElseThrow(() -> new AppelloNotFoundException(id_appello));
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var studente = studenteRepository.findByUsername(auth.getName()).orElseThrow();
        appello.addIscritto(studente);
        appelloRepository.save(appello);

        return ResponseEntity.created(linkTo(methodOn(AppelloController.class).one(id, id_appello)).toUri()).build();
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Could not find appello")
    public static class AppelloNotFoundException extends RuntimeException {
        public AppelloNotFoundException(Long id) {
            super("Could not find appello " + id);
        }
    }

    @ExceptionHandler(AppelloNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse handlePianoNotFound(AppelloNotFoundException ex) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }
}
