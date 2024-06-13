package com.beyondrest.SegreteriaVirtualeGraphQL.corso;

import com.beyondrest.SegreteriaVirtualeGraphQL.curriculum.Curriculum;
import com.beyondrest.SegreteriaVirtualeGraphQL.curriculum.CurriculumRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class CorsoController {
    private final CorsoRepository corsoRepository;
    private final CurriculumRepository curriculumRepository;

    CorsoController(CorsoRepository corsoRepository,
                    CurriculumRepository curriculumRepository) {
        this.corsoRepository = corsoRepository;
        this.curriculumRepository = curriculumRepository;
    }

    @QueryMapping
    public Corso corso(@Argument Long id) {
        return corsoRepository.findById(id).orElseThrow();
    }

    @QueryMapping
    public List<Corso> corsi() {
        return corsoRepository.findAll();
    }

    @BatchMapping(field = "curricula")
    public Map<Corso, List<Curriculum>> curricula(List<Corso> corsi) {
        var curricula = curriculumRepository.findAllByCorsoIn(corsi);
        return corsi.stream().collect(Collectors.toMap(corso -> corso, corso -> curricula.stream()
                .filter(curriculum -> curriculum.getCorso().equals(corso))
                .collect(Collectors.toList())));
    }
}
