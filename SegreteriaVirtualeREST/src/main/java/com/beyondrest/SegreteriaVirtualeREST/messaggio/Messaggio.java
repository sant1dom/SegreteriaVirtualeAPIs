package com.beyondrest.SegreteriaVirtualeREST.messaggio;

import com.beyondrest.SegreteriaVirtualeREST.docente.Docente;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

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
    @ManyToOne
    private Docente autore;
}
