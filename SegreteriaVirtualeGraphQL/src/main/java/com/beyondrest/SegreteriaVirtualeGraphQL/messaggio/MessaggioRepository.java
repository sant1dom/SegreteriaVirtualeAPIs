package com.beyondrest.SegreteriaVirtualeGraphQL.messaggio;

import com.beyondrest.SegreteriaVirtualeGraphQL.docente.Docente;
import com.beyondrest.SegreteriaVirtualeGraphQL.insegnamento.Insegnamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessaggioRepository extends JpaRepository<Messaggio, Long> {
    @Query("SELECT m FROM Messaggio m JOIN FETCH m.insegnamenti i WHERE i IN :insegnamenti")
    List<Messaggio> findAllByInsegnamenti(@Param("insegnamenti") List<Insegnamento> insegnamenti);
}
