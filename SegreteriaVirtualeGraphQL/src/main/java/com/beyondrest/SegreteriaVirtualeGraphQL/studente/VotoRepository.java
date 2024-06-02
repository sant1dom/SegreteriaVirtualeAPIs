package com.beyondrest.SegreteriaVirtualeGraphQL.studente;

import com.beyondrest.SegreteriaVirtualeGraphQL.insegnamento.Insegnamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface VotoRepository extends JpaRepository<Voto, Long> {
}
