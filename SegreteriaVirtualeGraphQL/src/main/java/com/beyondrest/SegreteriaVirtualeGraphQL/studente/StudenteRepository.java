package com.beyondrest.SegreteriaVirtualeGraphQL.studente;

import com.beyondrest.SegreteriaVirtualeGraphQL.appello.Appello;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudenteRepository extends JpaRepository<Studente, Long> {
    Optional<Studente> findByEmail(String userName);

    Optional<Studente> findByUsername(String username);

    Optional<Studente> findByEmailIgnoreCase(String email);

    @Query("SELECT s FROM Studente s JOIN FETCH s.appelli a WHERE a IN :appelli")
    List<Studente> findAllByAppelli(List<Appello> appelli);

}
