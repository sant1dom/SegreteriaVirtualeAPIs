package com.beyondrest.SegreteriaVirtualeGraphQL.insegnamento;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InsegnamentoRepository extends JpaRepository<Insegnamento, Long> {
    @Query("SELECT i FROM Insegnamento i LEFT JOIN FETCH i.bacheca WHERE i.id = :insegnamentoId")
    Optional<Insegnamento> findByIdWithBacheca(@Param("insegnamentoId") Long insegnamentoId);
}
