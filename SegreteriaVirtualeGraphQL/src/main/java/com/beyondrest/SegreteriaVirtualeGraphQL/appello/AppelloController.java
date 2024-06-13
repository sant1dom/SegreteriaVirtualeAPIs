package com.beyondrest.SegreteriaVirtualeGraphQL.appello;

import com.beyondrest.SegreteriaVirtualeGraphQL.insegnamento.InsegnamentoRepository;
import com.beyondrest.SegreteriaVirtualeGraphQL.studente.Studente;
import com.beyondrest.SegreteriaVirtualeGraphQL.studente.StudenteRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class AppelloController {
    private final AppelloRepository appelloRepository;
    private final InsegnamentoRepository insegnamentoRepository;
    private final StudenteRepository studenteRepository;

    AppelloController(AppelloRepository appelloRepository, InsegnamentoRepository insegnamentoRepository, StudenteRepository studenteRepository) {
        this.appelloRepository = appelloRepository;
        this.insegnamentoRepository = insegnamentoRepository;
        this.studenteRepository = studenteRepository;
    }

    @QueryMapping
    public List<Appello> appelli(@Argument Long insegnamentoId) {
        return insegnamentoRepository.findById(insegnamentoId).orElseThrow().getAppelli();
    }

    @QueryMapping
    public Appello appello(@Argument Long insegnamentoId, @Argument Long id) {
        return insegnamentoRepository.findById(insegnamentoId).orElseThrow().getAppelli().stream()
                .filter(appello -> appello.getId().equals(id))
                .findFirst()
                .orElseThrow();
    }

    @BatchMapping(field = "iscritti")
    public Map<Appello, Set<Studente>> iscritti(List<Appello> appelli) {
        var studenti = studenteRepository.findAllByAppelli(appelli);
        return appelli.stream().collect(Collectors.toMap(appello -> appello, appello -> studenti.stream()
                .filter(studente -> studente.getAppelli().contains(appello))
                .collect(Collectors.toSet())));
    }

}
