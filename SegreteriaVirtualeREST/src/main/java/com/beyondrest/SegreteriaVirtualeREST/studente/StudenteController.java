package com.beyondrest.SegreteriaVirtualeREST.studente;

import com.beyondrest.SegreteriaVirtualeREST.insegnamento.InsegnamentoController;
import com.beyondrest.SegreteriaVirtualeREST.pianodistudi.PianoDiStudiPrivato;
import com.beyondrest.SegreteriaVirtualeREST.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "Studente", description = "APIs related to Studente operations")
public class StudenteController {
    StudenteRepository studenteRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public StudenteController(AuthenticationManager authenticationManager, JwtService jwtService, StudenteRepository studenteRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.studenteRepository = studenteRepository;
    }

    @Operation(summary = "Get the libretto of the authenticated student")
    @GetMapping("/libretto")
    @Secured("ROLE_STUDENTE")
    @SecurityRequirement(name = "bearerAuth")
    public EntityModel<PianoDiStudiPrivato> one(HttpServletRequest request) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var pdsp = studenteRepository.findByUsername(auth.getName()).orElseThrow().getPianoDiStudiPrivato();
        var pdspModel = EntityModel.of(pdsp,
                linkTo(methodOn(StudenteController.class).one(request)).withSelfRel());
        pdsp.getInsegnamenti().forEach(insegnamento -> pdspModel.add(linkTo(methodOn(InsegnamentoController.class).one(insegnamento.getId())).withRel("insegnamenti")));
        return pdspModel;
    }

    @Operation(summary = "Authenticate a student")
    @PostMapping("/login")
    public String authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        if (!authentication.isAuthenticated()) {
            throw new RuntimeException("Invalid credentials");
        }
        Optional<Studente> studente = studenteRepository.findByUsername(authenticationRequest.getUsername());

        return jwtService.generateToken(studente.orElseThrow());
    }

    @Operation(summary = "Register a new student")
    @PostMapping("/registrazione")
    public String registrazione(@RequestBody Studente studente) {
        studenteRepository.save(studente);
        return jwtService.generateToken(studente);
    }
}
