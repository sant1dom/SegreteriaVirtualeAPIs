package com.beyondrest.SegreteriaVirtualeGraphQL.studente;

import com.beyondrest.SegreteriaVirtualeGraphQL.pianodistudi.PianoDiStudiPrivato;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@Controller
public class StudenteController {
    private final StudenteRepository studenteRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${application.security.jwt.secret-key}")
    private String jwtSecret;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpirationMs;

    StudenteController(StudenteRepository studenteRepository, PasswordEncoder passwordEncoder) {
        this.studenteRepository = studenteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @QueryMapping
    @PreAuthorize("hasRole('ROLE_STUDENTE')")
    public PianoDiStudiPrivato libretto(Principal principal) {
        return studenteRepository.findByEmailIgnoreCase(principal.getName()).orElseThrow().getPianoDiStudiPrivato();
    }

    @QueryMapping
    public String login(@Argument String username, @Argument String password) {
        Studente studente = studenteRepository.findByEmailIgnoreCase(username).orElseThrow();
        if (passwordEncoder.matches(password, studente.getPassword())) {
            return Jwts.builder()
                    .subject(username)
                    .claim("roles", List.of("ROLE_STUDENTE")) // Aggiungi i ruoli
                    .issuedAt(new Date())
                    .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                    .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .compact();
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }

    @MutationMapping
    public String registrazione(@Argument String nome,
                                @Argument String cognome,
                                @Argument String email,
                                @Argument String matricola,
                                @Argument String username,
                                @Argument String password) {
        Studente studente = new Studente();
        studente.setNome(nome);
        studente.setCognome(cognome);
        studente.setEmail(email);
        studente.setMatricola(matricola);
        studente.setUsername(username);
        studente.setPassword(passwordEncoder.encode(password));
        studenteRepository.save(studente);

        return Jwts.builder()
                .subject(username)
                .claim("roles", List.of("ROLE_STUDENTE"))
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();
    }
}
