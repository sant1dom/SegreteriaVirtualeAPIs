package com.beyondrest.SegreteriaVirtualeGraphQL.insegnamento;

import com.beyondrest.SegreteriaVirtualeGraphQL.appello.Appello;
import com.beyondrest.SegreteriaVirtualeGraphQL.appello.AppelloRepository;
import com.beyondrest.SegreteriaVirtualeGraphQL.docente.Docente;
import com.beyondrest.SegreteriaVirtualeGraphQL.docente.DocenteRepository;
import com.beyondrest.SegreteriaVirtualeGraphQL.lezione.Lezione;
import com.beyondrest.SegreteriaVirtualeGraphQL.lezione.LezioneRepository;
import com.beyondrest.SegreteriaVirtualeGraphQL.messaggio.Messaggio;
import com.beyondrest.SegreteriaVirtualeGraphQL.messaggio.MessaggioRepository;
import com.beyondrest.SegreteriaVirtualeGraphQL.studente.VotoRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class InsegnamentoController {
    private final InsegnamentoRepository insegnamentoRepository;
    private final LezioneRepository lezioneRepository;
    private final DocenteRepository docenteRepository;
    private final AppelloRepository appelloRepository;
    private final MessaggioRepository messaggioRepository;

    InsegnamentoController(InsegnamentoRepository insegnamentoRepository,
                           LezioneRepository lezioneRepository,
                           DocenteRepository docenteRepository,
                           AppelloRepository appelloRepository,
                           MessaggioRepository messaggioRepository) {
        this.insegnamentoRepository = insegnamentoRepository;
        this.lezioneRepository = lezioneRepository;
        this.docenteRepository = docenteRepository;
        this.appelloRepository = appelloRepository;
        this.messaggioRepository = messaggioRepository;
    }

    @QueryMapping
    public Insegnamento insegnamento(@Argument Long id) {
        return insegnamentoRepository.findById(id).orElseThrow();
    }

    @QueryMapping
    public List<Insegnamento> insegnamenti() {
        return insegnamentoRepository.findAll();
    }

    @BatchMapping(field = "lezioni")
    public Map<Insegnamento, List<Lezione>> lezioni(List<Insegnamento> insegnamenti) {
        return mapEntities(insegnamenti, lezioneRepository.findAllByInsegnamentoIn(insegnamenti));
    }

    @BatchMapping(field = "docenti")
    public Map<Insegnamento, List<Docente>> docenti(List<Insegnamento> insegnamenti) {
        List<Docente> docenti = docenteRepository.findAllByInsegnamenti(insegnamenti);
        return insegnamenti.stream()
                .collect(Collectors.toMap(
                        insegnamento -> insegnamento,
                        insegnamento -> docenti.stream()
                                .filter(docente -> docente.getInsegnamenti().contains(insegnamento))
                                .collect(Collectors.toList())
                ));
    }

    @BatchMapping(field = "appelli")
    public Map<Insegnamento, List<Appello>> appelli(List<Insegnamento> insegnamenti) {
        return mapEntities(insegnamenti, appelloRepository.findAllByInsegnamentoIn(insegnamenti));
    }

    @BatchMapping(field = "bacheca")
    public Map<Insegnamento, List<Messaggio>> bacheca(List<Insegnamento> insegnamenti) {
        List<Messaggio> messaggi = messaggioRepository.findAllByInsegnamenti(insegnamenti);
        return insegnamenti.stream()
                .collect(Collectors.toMap(
                        insegnamento -> insegnamento,
                        insegnamento -> messaggi.stream()
                                .filter(messaggio -> messaggio.getInsegnamenti().contains(insegnamento))
                                .collect(Collectors.toList())
                ));
    }


    private <T> Map<Insegnamento, List<T>> mapEntities(List<Insegnamento> insegnamenti, List<T> entities) {
        Map<Insegnamento, List<T>> insegnamentiMap = entities.stream()
                .collect(Collectors.groupingBy(entity -> {
                    if (entity instanceof Lezione) {
                        return ((Lezione) entity).getInsegnamento();
                    } else if (entity instanceof Appello) {
                        return ((Appello) entity).getInsegnamento();
                    } else {
                        throw new IllegalArgumentException("Unsupported type: " + entity.getClass().getName());
                    }
                }));

        insegnamenti.forEach(insegnamento -> insegnamentiMap.putIfAbsent(insegnamento, Collections.emptyList()));

        return insegnamentiMap;
    }
}
