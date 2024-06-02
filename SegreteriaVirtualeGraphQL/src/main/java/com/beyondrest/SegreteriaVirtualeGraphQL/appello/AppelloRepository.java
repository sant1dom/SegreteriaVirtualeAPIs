package com.beyondrest.SegreteriaVirtualeGraphQL.appello;

import com.beyondrest.SegreteriaVirtualeGraphQL.insegnamento.Insegnamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AppelloRepository extends JpaRepository<Appello, Long> {
    List<Appello> findAllByInsegnamentoIn(List<Insegnamento> insegnamenti);
}
