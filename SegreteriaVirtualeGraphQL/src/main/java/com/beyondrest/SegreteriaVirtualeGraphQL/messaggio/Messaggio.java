package com.beyondrest.SegreteriaVirtualeGraphQL.messaggio;

import com.beyondrest.SegreteriaVirtualeGraphQL.docente.Docente;
import com.beyondrest.SegreteriaVirtualeGraphQL.insegnamento.Insegnamento;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "messaggi")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class Messaggio {
    @Column(name = "messaggio_id")
    private @Id @GeneratedValue Long id;
    private String titolo;
    private String testo;
    @Temporal(TemporalType.DATE)
    private Date data;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id")
    @ToString.Exclude
    private Docente autore;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "bacheca",
            joinColumns = @JoinColumn(name = "messaggio_id"),
            inverseJoinColumns = @JoinColumn(name = "insegnamento_id"))
    @ToString.Exclude
    private List<Insegnamento> insegnamenti = List.of();
}
