package com.beyondrest.SegreteriaVirtualeREST.studente;

import com.beyondrest.SegreteriaVirtualeREST.ErrorResponse;
import com.beyondrest.SegreteriaVirtualeREST.insegnamento.InsegnamentoController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Table;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "Voto", description = "APIs related to Voto operations")
public class VotoController {
    StudenteRepository studenteRepository;

    public VotoController(StudenteRepository studenteRepository) {
        this.studenteRepository = studenteRepository;
    }

    @Operation(summary = "Get a specific voto by ID")
    @GetMapping("/voti/{id}")
    @Secured("ROLE_STUDENTE")
    @SecurityRequirement(name = "bearerAuth")
    public EntityModel<Voto> one(HttpServletRequest request,
                                 @PathVariable @Parameter(description = "ID of the voto", example = "1") Long id) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var studente = studenteRepository.findByUsername(auth.getName()).orElseThrow();
        var voto = studente.getPianoDiStudiPrivato().getVoti().stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new VotoNotFoundException(id));
        return EntityModel.of(voto, linkTo(methodOn(InsegnamentoController.class).one(voto.getInsegnamento().getId())).withRel("insegnamento"));
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Could not find voto")
    public static class VotoNotFoundException extends RuntimeException {
        public VotoNotFoundException(Long id) {
            super("Could not find voto " + id);
        }
    }

    @ExceptionHandler(VotoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse handleVotoNotFound(VotoNotFoundException ex) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }
}
