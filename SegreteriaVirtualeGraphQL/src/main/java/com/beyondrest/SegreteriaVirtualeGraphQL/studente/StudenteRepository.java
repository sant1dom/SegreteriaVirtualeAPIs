package com.beyondrest.SegreteriaVirtualeGraphQL.studente;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudenteRepository extends JpaRepository<Studente, Long> {
    Optional<Studente> findByEmail(String userName);

    Optional<Studente> findByUsername(String username);

    Optional<Studente> findByEmailIgnoreCase(String email);

}
