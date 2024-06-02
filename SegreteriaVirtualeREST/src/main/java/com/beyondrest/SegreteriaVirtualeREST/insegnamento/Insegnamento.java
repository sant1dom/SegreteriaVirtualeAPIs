package com.beyondrest.SegreteriaVirtualeREST.insegnamento;

import com.beyondrest.SegreteriaVirtualeREST.appello.Appello;
import com.beyondrest.SegreteriaVirtualeREST.docente.Docente;
import com.beyondrest.SegreteriaVirtualeREST.lezione.Lezione;
import com.beyondrest.SegreteriaVirtualeREST.messaggio.Messaggio;
import com.beyondrest.SegreteriaVirtualeREST.studente.Voto;
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
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "insegnamento_lezione",
            joinColumns = @JoinColumn(name = "insegnamento_id"),
            inverseJoinColumns = @JoinColumn(name = "lezione_id"))
    @JsonBackReference
    private List<Lezione> lezioni;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "insegnamento_docente",
            joinColumns = @JoinColumn(name = "insegnamento_id"),
            inverseJoinColumns = @JoinColumn(name = "docente_id"))
    @JsonBackReference
    private List<Docente> docenti;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "bacheca",
            joinColumns = @JoinColumn(name = "insegnamento_id"),
            inverseJoinColumns = @JoinColumn(name = "messaggio_id"))
    @JsonBackReference
    private List<Messaggio> bacheca;

    @ToString.Exclude
    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "appello_id")
    private List<Appello> appelli;

    @ToString.Exclude
    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "voto_id")
    private List<Voto> voti;

    public Insegnamento(String nome, String descrizione, int cfu, String anno, String orario) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.cfu = cfu;
        this.anno = anno;
        this.orario = orario;
    }
}
