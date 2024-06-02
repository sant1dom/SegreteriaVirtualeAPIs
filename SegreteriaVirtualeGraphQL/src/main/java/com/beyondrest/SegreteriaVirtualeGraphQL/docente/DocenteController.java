package com.beyondrest.SegreteriaVirtualeGraphQL.docente;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class DocenteController {
    private final DocenteRepository docenteRepository;

    DocenteController(DocenteRepository docenteRepository) {
        this.docenteRepository = docenteRepository;
    }

    @QueryMapping
    public List<Docente> docenti() {
        return docenteRepository.findAll();
    }

    @QueryMapping
    public Docente docente(@Argument Long id) {
        return docenteRepository.findById(id).orElseThrow();
    }
}
