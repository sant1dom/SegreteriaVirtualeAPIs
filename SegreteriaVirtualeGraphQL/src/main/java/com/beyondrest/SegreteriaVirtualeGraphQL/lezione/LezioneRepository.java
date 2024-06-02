package com.beyondrest.SegreteriaVirtualeGraphQL.lezione;

import com.beyondrest.SegreteriaVirtualeGraphQL.insegnamento.Insegnamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LezioneRepository extends JpaRepository<Lezione, Long> {
    List<Lezione> findAllByInsegnamentoIn(List<Insegnamento> insegnamenti);
}
