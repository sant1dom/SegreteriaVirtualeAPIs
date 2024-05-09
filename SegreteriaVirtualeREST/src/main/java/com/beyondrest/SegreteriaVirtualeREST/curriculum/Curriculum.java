package com.beyondrest.SegreteriaVirtualeREST.curriculum;

import com.beyondrest.SegreteriaVirtualeREST.pianodistudi.PianoDiStudi;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Entity
@Table(name = "curricula")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class Curriculum {
    @JsonIgnore
    @Column(name = "curriculum_id")
    private @Id @GeneratedValue Long id;
    private String nome;
    private String descrizione;
    @OneToMany(fetch = FetchType.EAGER)
    @JsonBackReference
    private List<PianoDiStudi> pianoDiStudi;
}