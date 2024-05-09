package com.beyondrest.SegreteriaVirtualeREST.lezione;

import com.beyondrest.SegreteriaVirtualeREST.insegnamento.Insegnamento;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
@Entity
@Table(name = "lezioni")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class Lezione {
    @Column(name = "lezione_id")
    private @Id @GeneratedValue Long id;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "HH:mm:ss dd/MM/yyyy")
    private Date data;
    private String diarioDellaLezione;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "insegnamento_id")
    @JsonBackReference
    private Insegnamento insegnamento;

    public Lezione(long id, Date date, String diarioDellaLezione) {
        this.id = id;
        this.data = date;
        this.diarioDellaLezione = diarioDellaLezione;
    }
}
