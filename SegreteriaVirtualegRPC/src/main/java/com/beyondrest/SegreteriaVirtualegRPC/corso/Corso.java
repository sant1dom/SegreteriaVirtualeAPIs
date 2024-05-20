package com.beyondrest.SegreteriaVirtualegRPC.corso;

import com.beyondrest.SegreteriaVirtualegRPC.curriculum.Curriculum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "corsi")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class Corso {
    @JsonIgnore
    @Id
    @GeneratedValue
    @Column(name = "corso_id")
    private Long id;
    private String nome;
    private String descrizione;
    private String anno;
    @OneToMany(fetch = FetchType.EAGER)
    @JsonBackReference
    private List<Curriculum> curricula;
}