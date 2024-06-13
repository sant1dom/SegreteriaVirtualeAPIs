package com.beyondrest.SegreteriaVirtualeGraphQL.studente;

import com.beyondrest.SegreteriaVirtualeGraphQL.appello.Appello;
import com.beyondrest.SegreteriaVirtualeGraphQL.pianodistudi.PianoDiStudiPrivato;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
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
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "piano_id")
    @JsonBackReference
    @ToString.Exclude
    private PianoDiStudiPrivato pianoDiStudiPrivato;
    private Set<Ruolo> ruoli = Set.of(Ruolo.ROLE_STUDENTE);

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "appello_studente",
            joinColumns = @JoinColumn(name = "studente_id"),
            inverseJoinColumns = @JoinColumn(name = "appello_id"))
    @JsonBackReference
    @ToString.Exclude
    private Set<Appello> appelli;

    public Studente(String nome, String cognome, String email, String matricola, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.matricola = matricola;
        this.username = nome + "." + cognome + "." + matricola;
        this.password = new BCryptPasswordEncoder().encode(password);
    }
}
