package com.beyondrest.SegreteriaVirtualegRPC.corso;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CorsoRepository extends JpaRepository<Corso, Long> {
    @Query("SELECT c FROM Corso c JOIN FETCH c.curricula WHERE c.id = :id")
    public Optional<Corso> findByIdWithCurricula(Long id);

    @Query("SELECT c FROM Corso c JOIN FETCH c.curricula cur JOIN FETCH cur.pianoDiStudi WHERE c.id = :id")
    public Optional<Corso> findByIdWithCurriculaAndPianiDiStudi(Long id);
}
