package com.beyondrest.SegreteriaVirtualeGraphQL.insegnamento;

import com.beyondrest.SegreteriaVirtualeGraphQL.appello.Appello;
import com.beyondrest.SegreteriaVirtualeGraphQL.docente.Docente;
import com.beyondrest.SegreteriaVirtualeGraphQL.lezione.Lezione;
import com.beyondrest.SegreteriaVirtualeGraphQL.messaggio.Messaggio;
import com.beyondrest.SegreteriaVirtualeGraphQL.studente.Voto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "insegnamenti")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class Insegnamento {
    @JsonIgnore
    @Column(name = "insegnamento_id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nome;
    private String descrizione;
    private int cfu;
    private String anno;
    private String orario;

    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "insegnamento_id")
    private List<Lezione> lezioni = List.of();

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "insegnamento_docente",
            joinColumns = @JoinColumn(name = "insegnamento_id"),
            inverseJoinColumns = @JoinColumn(name = "docente_id"))
    @JsonBackReference
    private List<Docente> docenti = List.of();

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "bacheca",
            joinColumns = @JoinColumn(name = "insegnamento_id"),
            inverseJoinColumns = @JoinColumn(name = "messaggio_id"))
    @JsonBackReference
    private List<Messaggio> bacheca = List.of();

    @ToString.Exclude
    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "appello_id")
    private List<Appello> appelli = List.of();

    @ToString.Exclude
    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "voto_id")
    private List<Voto> voti = List.of();

    public Insegnamento(String nome, String descrizione, int cfu, String anno, String orario) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.cfu = cfu;
        this.anno = anno;
        this.orario = orario;
    }
}
