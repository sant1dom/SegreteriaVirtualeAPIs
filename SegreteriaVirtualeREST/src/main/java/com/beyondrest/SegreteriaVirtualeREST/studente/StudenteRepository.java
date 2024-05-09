package com.beyondrest.SegreteriaVirtualeREST.studente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface StudenteRepository extends JpaRepository<Studente, Long> {
    Optional<Studente> findByEmail(String userName);

    Optional<Studente> findByUsername(String username);
}
