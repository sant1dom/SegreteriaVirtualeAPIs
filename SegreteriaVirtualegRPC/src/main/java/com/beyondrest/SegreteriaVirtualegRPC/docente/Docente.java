package com.beyondrest.SegreteriaVirtualegRPC.docente;

import com.beyondrest.SegreteriaVirtualegRPC.insegnamento.Insegnamento;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "docenti")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class Docente {
    @JsonIgnore
    @Column(name = "docente_id") @Id @GeneratedValue private Long id;
    private String nome;
    private String cognome;
    private String email;
    private String telefono;

    @JsonBackReference
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "insegnamento_docente",
            joinColumns = @JoinColumn(name = "docente_id"),
            inverseJoinColumns = @JoinColumn(name = "insegnamento_id"))
    private List<Insegnamento> insegnamenti;

    public Docente(String nome, String cognome, String email, String telefono) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.telefono = telefono;
    }
}
