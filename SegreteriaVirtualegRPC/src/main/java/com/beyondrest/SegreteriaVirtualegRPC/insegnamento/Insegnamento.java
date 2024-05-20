package com.beyondrest.SegreteriaVirtualegRPC.insegnamento;

import com.beyondrest.SegreteriaVirtualegRPC.appello.Appello;
import com.beyondrest.SegreteriaVirtualegRPC.docente.Docente;
import com.beyondrest.SegreteriaVirtualegRPC.lezione.Lezione;
import com.beyondrest.SegreteriaVirtualegRPC.messaggio.Messaggio;
import com.beyondrest.SegreteriaVirtualegRPC.studente.Voto;
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
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "insegnamento_lezione",
            joinColumns = @JoinColumn(name = "insegnamento_id"),
            inverseJoinColumns = @JoinColumn(name = "lezione_id"))
    @JsonBackReference
    private List<Lezione> lezioni;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "insegnamento_docente",
            joinColumns = @JoinColumn(name = "insegnamento_id"),
            inverseJoinColumns = @JoinColumn(name = "docente_id"))
    @JsonBackReference
    private List<Docente> docenti;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "bacheca",
            joinColumns = @JoinColumn(name = "insegnamento_id"),
            inverseJoinColumns = @JoinColumn(name = "messaggio_id"))
    @JsonBackReference
    private List<Messaggio> bacheca;

    @ToString.Exclude
    @JsonBackReference
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "appello_id")
    private List<Appello> appelli;

    @ToString.Exclude
    @JsonBackReference
    @OneToMany(fetch = FetchType.EAGER)
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
