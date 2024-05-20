package com.beyondrest.SegreteriaVirtualegRPC.appello;

import com.beyondrest.SegreteriaVirtualegRPC.insegnamento.Insegnamento;
import com.beyondrest.SegreteriaVirtualegRPC.studente.Studente;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "appelli")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class Appello {
    @JsonIgnore
    @Id
    @GeneratedValue
    @Column(name = "appello_id")
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "HH:mm:ss dd/MM/yyyy")
    private Date data;
    private String aula;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "insegnamento_id")
    @JsonBackReference
    private Insegnamento insegnamento;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "appello_studente",
            joinColumns = @JoinColumn(name = "appello_id"),
            inverseJoinColumns = @JoinColumn(name = "studente_id"))
    @JsonBackReference
    private Set<Studente> iscritti = new HashSet<>();

    public Appello(Date date, String aula, Insegnamento insegnamento) {
        this.data = date;
        this.aula = aula;
        this.insegnamento = insegnamento;
    }

    public void addIscritto(Studente studente) {
        iscritti.add(studente);
    }
}
