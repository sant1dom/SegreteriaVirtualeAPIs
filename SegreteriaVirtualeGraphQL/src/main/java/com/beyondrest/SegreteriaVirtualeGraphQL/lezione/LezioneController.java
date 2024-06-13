package com.beyondrest.SegreteriaVirtualeGraphQL.lezione;

import com.beyondrest.SegreteriaVirtualeGraphQL.insegnamento.Insegnamento;
import com.beyondrest.SegreteriaVirtualeGraphQL.insegnamento.InsegnamentoRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class LezioneController {
    private final InsegnamentoRepository insegnamentoRepository;

    public LezioneController(InsegnamentoRepository insegnamentoRepository) {
        this.insegnamentoRepository = insegnamentoRepository;
    }

    @QueryMapping
    public List<Lezione> lezioni(@Argument Long insegnamentoId) {
        return insegnamentoRepository.findById(insegnamentoId).orElseThrow().getLezioni();
    }

    @QueryMapping
    public Lezione lezione(@Argument Long insegnamentoId, @Argument Long id) {
        return insegnamentoRepository.findById(insegnamentoId).orElseThrow().getLezioni().stream()
                .filter(lezione -> lezione.getId().equals(id))
                .findFirst()
                .orElseThrow();
    }
}
