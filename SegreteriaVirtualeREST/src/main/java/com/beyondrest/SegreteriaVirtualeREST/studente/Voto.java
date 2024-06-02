package com.beyondrest.SegreteriaVirtualeREST.studente;

import com.beyondrest.SegreteriaVirtualeREST.insegnamento.Insegnamento;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "voto")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class Voto {
    @Column(name = "voto_id")
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(columnDefinition = "TEXT CHECK (votazione IN ('30L', '18', '19', '20', '21', '22', '23', '24', '25', '26', '27', '28', '29', '30'))")
    private String votazione;
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insegnamento_id")
    private Insegnamento insegnamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studente_id")
    @JsonBackReference
    private Studente studente;

    public Voto(String votazione, Date data, Insegnamento insegnamento, Studente studente) {
        this.votazione = votazione;
        this.data = data;
        this.insegnamento = insegnamento;
        this.studente = studente;
    }
}
