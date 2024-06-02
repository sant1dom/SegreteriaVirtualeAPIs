package com.beyondrest.SegreteriaVirtualeGraphQL.pianodistudi;

import com.beyondrest.SegreteriaVirtualeGraphQL.insegnamento.Insegnamento;
import com.beyondrest.SegreteriaVirtualeGraphQL.studente.Studente;
import com.beyondrest.SegreteriaVirtualeGraphQL.studente.Voto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "piano_di_studi_privato")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class PianoDiStudiPrivato extends PianoDiStudi{

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "voto_id")
    private List<Voto> voti;

    @ToString.Exclude
    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studente_id")
    private Studente studente;

    public PianoDiStudiPrivato(String anno, List<Insegnamento> insegnamenti, List<Voto> voti, Studente studente) {
        super(anno, insegnamenti);
        this.voti = voti;
        this.studente = studente;
    }
}
