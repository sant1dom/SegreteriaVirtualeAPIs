package com.beyondrest.SegreteriaVirtualeGraphQL.docente;

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
public class DocenteController {
    private final DocenteRepository docenteRepository;
    private final InsegnamentoRepository insegnamentoRepository;

    DocenteController(DocenteRepository docenteRepository,
                      InsegnamentoRepository insegnamentoRepository) {
        this.docenteRepository = docenteRepository;
        this.insegnamentoRepository = insegnamentoRepository;
    }

    @QueryMapping
    public List<Docente> docenti() {
        return docenteRepository.findAll();
    }

    @QueryMapping
    public Docente docente(@Argument Long id) {
        return docenteRepository.findById(id).orElseThrow();
    }

    @BatchMapping(field = "insegnamenti")
    public Map<Docente, List<Insegnamento>> insegnamenti(List<Docente> docenti) {
        var insegnamenti = insegnamentoRepository.findAllByDocenti(docenti);
        return docenti.stream().collect(Collectors.toMap(docente -> docente, docente -> insegnamenti.stream()
                .filter(insegnamento -> insegnamento.getDocenti().contains(docente))
                .collect(Collectors.toList())));
    }
}
