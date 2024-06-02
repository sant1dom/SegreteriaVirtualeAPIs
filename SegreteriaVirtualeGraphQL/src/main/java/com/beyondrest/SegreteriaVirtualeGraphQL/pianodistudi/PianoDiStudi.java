package com.beyondrest.SegreteriaVirtualeGraphQL.pianodistudi;

import com.beyondrest.SegreteriaVirtualeGraphQL.insegnamento.Insegnamento;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "piano_di_studi")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class PianoDiStudi {
    @Column(name = "piano_di_studi_id")
    @JsonIgnore
    private @Id @GeneratedValue Long id;
    private String anno;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "piano_di_studi_insegnamento",
            joinColumns = @JoinColumn(name = "piano_di_studi_id"),
            inverseJoinColumns = @JoinColumn(name = "insegnamento_id"))
    @JsonBackReference
    @ToString.Exclude
    private List<Insegnamento> insegnamenti;

    public PianoDiStudi(String anno, List<Insegnamento> insegnamenti) {
        this.anno = anno;
        this.insegnamenti = insegnamenti;
    }
}
