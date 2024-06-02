package com.beyondrest.SegreteriaVirtualeGraphQL.appello;

import com.beyondrest.SegreteriaVirtualeGraphQL.insegnamento.InsegnamentoRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AppelloController {
    private final AppelloRepository appelloRepository;
    private final InsegnamentoRepository insegnamentoRepository;

    AppelloController(AppelloRepository appelloRepository, InsegnamentoRepository insegnamentoRepository) {
        this.appelloRepository = appelloRepository;
        this.insegnamentoRepository = insegnamentoRepository;
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
}
