package com.beyondrest.SegreteriaVirtualegRPC.curriculum;

import com.beyondrest.SegreteriaVirtualegRPC.pianodistudi.PianoDiStudi;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "curriculum")
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
    @OneToMany(fetch = FetchType.LAZY)
    @JsonBackReference
    @ToString.Exclude
    private Set<PianoDiStudi> pianiDiStudi;
}
