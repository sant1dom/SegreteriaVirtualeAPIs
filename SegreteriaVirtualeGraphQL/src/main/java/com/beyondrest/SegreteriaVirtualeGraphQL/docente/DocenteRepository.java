package com.beyondrest.SegreteriaVirtualeGraphQL.docente;

import com.beyondrest.SegreteriaVirtualeGraphQL.insegnamento.Insegnamento;
import com.beyondrest.SegreteriaVirtualeGraphQL.messaggio.Messaggio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocenteRepository extends JpaRepository<Docente, Long> {
    @Query("SELECT d FROM Docente d JOIN FETCH d.insegnamenti i WHERE i IN :insegnamenti")
    List<Docente> findAllByInsegnamenti(@Param("insegnamenti") List<Insegnamento> insegnamenti);
}
