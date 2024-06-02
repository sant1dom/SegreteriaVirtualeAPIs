package com.beyondrest.SegreteriaVirtualeGraphQL.corso;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class CorsoController {
    private final CorsoRepository corsoRepository;

    CorsoController(CorsoRepository corsoRepository) {
        this.corsoRepository = corsoRepository;
    }

    @QueryMapping
    public Corso corso(@Argument Long id) {
        return corsoRepository.findById(id).orElseThrow();
    }

    @QueryMapping
    public List<Corso> corsi() {
        return corsoRepository.findAll();
    }
}
