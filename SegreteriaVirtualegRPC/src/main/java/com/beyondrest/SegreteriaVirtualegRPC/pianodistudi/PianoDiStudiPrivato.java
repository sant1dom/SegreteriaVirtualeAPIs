package com.beyondrest.SegreteriaVirtualegRPC.pianodistudi;

import com.beyondrest.SegreteriaVirtualegRPC.insegnamento.Insegnamento;
import com.beyondrest.SegreteriaVirtualegRPC.studente.Studente;
import com.beyondrest.SegreteriaVirtualegRPC.studente.Voto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
