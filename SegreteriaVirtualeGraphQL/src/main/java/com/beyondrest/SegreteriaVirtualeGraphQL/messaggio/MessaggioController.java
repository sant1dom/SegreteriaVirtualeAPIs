package com.beyondrest.SegreteriaVirtualeGraphQL.messaggio;

import com.beyondrest.SegreteriaVirtualeGraphQL.docente.Docente;
import com.beyondrest.SegreteriaVirtualeGraphQL.docente.DocenteRepository;
import com.beyondrest.SegreteriaVirtualeGraphQL.insegnamento.InsegnamentoRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MessaggioController {
    private final InsegnamentoRepository insegnamentoRepository;
    private final DocenteRepository docenteRepository;

    public MessaggioController(InsegnamentoRepository insegnamentoRepository, DocenteRepository docenteRepository) {
        this.insegnamentoRepository = insegnamentoRepository;
        this.docenteRepository = docenteRepository;
    }

    @QueryMapping
    public List<Messaggio> messaggi(@Argument Long insegnamentoId) {
        return insegnamentoRepository.findById(insegnamentoId).orElseThrow().getBacheca();
    }

    @QueryMapping
    public Messaggio messaggio(@Argument Long insegnamentoId, @Argument Long id) {
        return insegnamentoRepository.findById(insegnamentoId).orElseThrow().getBacheca().stream()
                .filter(messaggio -> messaggio.getId().equals(id))
                .findFirst()
                .orElseThrow();
    }

    @BatchMapping(field = "autore")
    public Map<Messaggio, Docente> docente(List<Messaggio> messaggi) {
        var messaggiMap = docenteRepository.findAllByMessaggi(messaggi).stream().collect(Collectors.toMap(Docente::getId, docente -> docente));
        return messaggi.stream().collect(Collectors.toMap(messaggio -> messaggio, messaggio -> messaggiMap.get(messaggio.getAutore().getId())));
    }

    @SubscriptionMapping
    @Transactional
    public Flux<List<Messaggio>> messaggiSubscription(@Argument Long insegnamentoId) {
        var insegnamento = insegnamentoRepository.findById(insegnamentoId).orElseThrow();
        Hibernate.initialize(insegnamento.getBacheca());
        return Mono.fromCallable(insegnamento::getBacheca)
                .flatMapMany(bacheca -> {
                    Flux<List<Messaggio>> messaggiIniziali = Flux.fromIterable(bacheca).buffer();

                    final Long[] lastMessageId = {bacheca.isEmpty() ? 0 : bacheca.get(bacheca.size() - 1).getId()};

                    // Poll the database for new messages every 10 seconds
                    Flux<List<Messaggio>> newMessages = Flux.interval(Duration.ofSeconds(10))
                            .flatMap(i -> controllaNuoviMessaggi(insegnamentoId, lastMessageId[0]))
                            .doOnNext(messaggi -> {
                                if (!messaggi.isEmpty()) {
                                    lastMessageId[0] = messaggi.get(messaggi.size() - 1).getId();
                                }
                            });

                    return Flux.concat(messaggiIniziali, newMessages);
                });
    }


    private Flux<List<Messaggio>> controllaNuoviMessaggi(Long insegnamentoId, Long lastMessageId) {
        return Mono.fromCallable(() -> insegnamentoRepository.findByIdWithBacheca(insegnamentoId).orElseThrow().getBacheca())
                .flatMapMany(bacheca -> {
                    List<Messaggio> nuoviMessaggi = bacheca.stream()
                            .filter(messaggio -> messaggio.getId() > lastMessageId)
                            .toList();
                    return nuoviMessaggi.isEmpty() ? Flux.empty() : Flux.just(nuoviMessaggi);
                });
    }
}
