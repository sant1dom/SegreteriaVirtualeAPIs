package com.beyondrest.SegreteriaVirtualegRPC.studente;

import com.beyondrest.SegreteriaVirtualegRPC.corso.Corso;
import com.beyondrest.SegreteriaVirtualegRPC.curriculum.Curriculum;
import com.beyondrest.SegreteriaVirtualegRPC.pianodistudi.PianoDiStudi;
import com.beyondrest.SegreteriaVirtualegRPC.pianodistudi.PianoDiStudiPrivato;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Role;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@Entity
@Table(name = "studenti")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class Studente {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    @Column(name = "studente_id")
    private Long id;
    private String nome;
    private String cognome;
    @Column(unique = true)
    private String email;
    private String password;
    @Column(unique = true)
    private String matricola;
    @Column(unique = true)
    private String username;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "piano_id")
    @JsonBackReference
    @ToString.Exclude
    private PianoDiStudiPrivato pianoDiStudiPrivato;
    private Set<Ruolo> ruoli = Set.of(Ruolo.ROLE_STUDENTE);

    public Studente(String nome, String cognome, String email, String matricola, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.matricola = matricola;
        this.username = nome + "." + cognome + "." + matricola;
        this.password = new BCryptPasswordEncoder().encode(password);
    }
}
