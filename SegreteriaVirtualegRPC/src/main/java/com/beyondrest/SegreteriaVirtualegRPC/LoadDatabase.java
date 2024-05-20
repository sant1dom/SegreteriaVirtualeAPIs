package com.beyondrest.SegreteriaVirtualegRPC;


import com.beyondrest.SegreteriaVirtualegRPC.appello.Appello;
import com.beyondrest.SegreteriaVirtualegRPC.appello.AppelloRepository;
import com.beyondrest.SegreteriaVirtualegRPC.corso.CorsoRepository;
import com.beyondrest.SegreteriaVirtualegRPC.corso.Corso;
import com.beyondrest.SegreteriaVirtualegRPC.curriculum.Curriculum;
import com.beyondrest.SegreteriaVirtualegRPC.curriculum.CurriculumRepository;
import com.beyondrest.SegreteriaVirtualegRPC.docente.Docente;
import com.beyondrest.SegreteriaVirtualegRPC.docente.DocenteRepository;
import com.beyondrest.SegreteriaVirtualegRPC.insegnamento.InsegnamentoRepository;
import com.beyondrest.SegreteriaVirtualegRPC.insegnamento.Insegnamento;
import com.beyondrest.SegreteriaVirtualegRPC.lezione.Lezione;
import com.beyondrest.SegreteriaVirtualegRPC.lezione.LezioneRepository;
import com.beyondrest.SegreteriaVirtualegRPC.messaggio.Messaggio;
import com.beyondrest.SegreteriaVirtualegRPC.messaggio.MessaggioRepository;
import com.beyondrest.SegreteriaVirtualegRPC.pianodistudi.PianoDiStudi;
import com.beyondrest.SegreteriaVirtualegRPC.pianodistudi.PianoDiStudiPrivato;
import com.beyondrest.SegreteriaVirtualegRPC.pianodistudi.PianoDiStudiPrivatoRepository;
import com.beyondrest.SegreteriaVirtualegRPC.pianodistudi.PianoDiStudiRepository;
import com.beyondrest.SegreteriaVirtualegRPC.studente.Studente;
import com.beyondrest.SegreteriaVirtualegRPC.studente.StudenteRepository;
import com.beyondrest.SegreteriaVirtualegRPC.studente.Voto;
import com.beyondrest.SegreteriaVirtualegRPC.studente.VotoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(CorsoRepository corsoRepository,
                      CurriculumRepository curriculumRepository,
                      InsegnamentoRepository insegnamentoRepository,
                      DocenteRepository docenteRepository,
                      MessaggioRepository messaggioRepository,
                      LezioneRepository lezioneRepository,
                      PianoDiStudiRepository pianoDiStudiRepository,
                      AppelloRepository appelloRepository,
                      StudenteRepository studenteRepository,
                      PianoDiStudiPrivatoRepository pianoDiStudiPrivatoRepository,
                      VotoRepository votoRepository
    ) {
        return args -> {
            initializeDocenti(docenteRepository);
            initializeMessaggi(messaggioRepository, docenteRepository);
            initializeLezioni(lezioneRepository);
            initializeInsegnamenti(insegnamentoRepository, docenteRepository, messaggioRepository, lezioneRepository);
            initializeAppelli(insegnamentoRepository, appelloRepository);
            initializePianoDiStudi(pianoDiStudiRepository, insegnamentoRepository);
            initializeCurricula(curriculumRepository, insegnamentoRepository, pianoDiStudiRepository);
            initializeCorsi(corsoRepository, curriculumRepository);
            initializeStudenti(studenteRepository, insegnamentoRepository, pianoDiStudiPrivatoRepository, votoRepository);
        };
    }

    private static void initializeStudenti(StudenteRepository studenteRepository,
                                           InsegnamentoRepository insegnamentoRepository,
                                           PianoDiStudiPrivatoRepository pianoDiStudiPrivatoRepository,
                                           VotoRepository votoRepository) {
        var names = List.of("Mario", "Luca", "Giuseppe", "Giovanni");
        var surnames = List.of("Rossi", "Bianchi", "Verdi", "Neri");
        for (int i = 0; i < 4; i++) {
            var studente = new Studente(names.get(i), surnames.get(i), names.get(i) + "." + surnames.get(i) + "@student.univaq.it", String.valueOf(100000 + i), "123456");
            var std = studenteRepository.save(studente);
            var pianoDiStudiPrivato = new PianoDiStudiPrivato("2024-2025", List.of(insegnamentoRepository.findById(1L).orElseGet(Insegnamento::new), insegnamentoRepository.findById(4L).orElseGet(Insegnamento::new)), new ArrayList<>(), std);
            var voto = new Voto((long) i,
                    "30L",
                    new Date(System.currentTimeMillis()),
                    insegnamentoRepository.findById(1L).orElseGet(Insegnamento::new),
                    studente);
            votoRepository.save(voto);
            pianoDiStudiPrivato.setVoti(List.of(voto));
            System.out.println(pianoDiStudiPrivato);
            var pdsp = pianoDiStudiPrivatoRepository.save(pianoDiStudiPrivato);
            std.setPianoDiStudiPrivato(pdsp);
            log.info("Preloading " + studenteRepository.save(std));
        }
    }

    private static void initializeAppelli(InsegnamentoRepository insegnamentoRepository, AppelloRepository appelloRepository) {
        for (int i = 1; i <= 6; i++) {
            var insegnamento = insegnamentoRepository.findById((long) i).orElseGet(Insegnamento::new);
            var appello = new Appello(new Date(System.currentTimeMillis()), "Aula A1." + i + " Coppito 0", insegnamento);
            log.info("Preloading " + appelloRepository.save(appello));
            insegnamento.getAppelli().add(appello);
            insegnamentoRepository.save(insegnamento);
        }
    }

    private static void initializePianoDiStudi(PianoDiStudiRepository pianoDiStudiRepository, InsegnamentoRepository insegnamentoRepository) {
        log.info("Preloading " + pianoDiStudiRepository.save(new PianoDiStudi(1L, "2023-2024", List.of(insegnamentoRepository.findById(5L).orElseGet(Insegnamento::new), insegnamentoRepository.findById(6L).orElseGet(Insegnamento::new)))));
        log.info("Preloading " + pianoDiStudiRepository.save(new PianoDiStudi(2L, "2024-2025", List.of(insegnamentoRepository.findById(1L).orElseGet(Insegnamento::new), insegnamentoRepository.findById(2L).orElseGet(Insegnamento::new)))));
        log.info("Preloading " + pianoDiStudiRepository.save(new PianoDiStudi(3L, "2024-2025", List.of(insegnamentoRepository.findById(3L).orElseGet(Insegnamento::new), insegnamentoRepository.findById(4L).orElseGet(Insegnamento::new)))));
    }

    private static void initializeLezioni(LezioneRepository lezioneRepository) {
        log.info("Preloading " + lezioneRepository.save(new Lezione(1L, new Date(System.currentTimeMillis()), "Diario della lezione 1")));
        log.info("Preloading " + lezioneRepository.save(new Lezione(2L, new Date(System.currentTimeMillis()), "Diario della lezione 2")));
        log.info("Preloading " + lezioneRepository.save(new Lezione(3L, new Date(System.currentTimeMillis()), "Diario della lezione 3")));
        log.info("Preloading " + lezioneRepository.save(new Lezione(4L, new Date(System.currentTimeMillis()), "Diario della lezione 4")));
    }

    private static void initializeMessaggi(MessaggioRepository messaggioRepository, DocenteRepository docenteRepository) {
        log.info("Preloading " + messaggioRepository.save(new Messaggio(1L, "Messaggio 1", "Testo messaggio 1", new Date(System.currentTimeMillis()), docenteRepository.findById(1L).orElseGet(Docente::new))));
        log.info("Preloading " + messaggioRepository.save(new Messaggio(2L, "Messaggio 2", "Testo messaggio 2", new Date(System.currentTimeMillis()), docenteRepository.findById(2L).orElseGet(Docente::new))));
        log.info("Preloading " + messaggioRepository.save(new Messaggio(3L, "Messaggio 3", "Testo messaggio 3", new Date(System.currentTimeMillis()), docenteRepository.findById(3L).orElseGet(Docente::new))));
        log.info("Preloading " + messaggioRepository.save(new Messaggio(4L, "Messaggio 4", "Testo messaggio 4", new Date(System.currentTimeMillis()), docenteRepository.findById(4L).orElseGet(Docente::new))));
    }

    private static void initializeDocenti(DocenteRepository docenteRepository) {
        log.info("Preloading " + docenteRepository.save(new Docente("Mario", "Rossi", "mario.rossi@univaq.it", "123456")));
        log.info("Preloading " + docenteRepository.save(new Docente("Luca", "Bianchi", "luca.bianchi@univaq.it", "123456")));
        log.info("Preloading " + docenteRepository.save(new Docente("Giuseppe", "Verdi", "giuseppe.verdi@univaq.it", "123456")));
        log.info("Preloading " + docenteRepository.save(new Docente("Giovanni", "Neri", "giovanni.neri@univaq.it", "123456")));
    }

    private static void initializeInsegnamenti(InsegnamentoRepository insegnamentoRepository,
                                               DocenteRepository docenteRepository,
                                               MessaggioRepository messaggioRepository,
                                               LezioneRepository lezioneRepository
    ) {
        var insegnamento1 = new Insegnamento("Analisi 1",
                "Corso di analisi 1",
                9,
                "2024-2025",
                "Lunedì 10:00 - 12:00, Mercoledì 10:00 - 12:00");
        insegnamento1.setLezioni(List.of(lezioneRepository.findById(1L).orElseGet(Lezione::new)));
        insegnamento1.setDocenti(List.of(docenteRepository.findById(1L).orElseGet(Docente::new), docenteRepository.findById(2L).orElseGet(Docente::new)));
        insegnamento1.setBacheca(List.of(messaggioRepository.findById(1L).orElseGet(Messaggio::new), messaggioRepository.findById(2L).orElseGet(Messaggio::new)));
        log.info("Preloading " + insegnamentoRepository.save(insegnamento1));

        var insegnamento2 = new Insegnamento("Analisi 2",
                "Corso di analisi 2",
                9,
                "2024-2025",
                "Lunedì 10:00 - 12:00, Mercoledì 10:00 - 12:00");
        insegnamento2.setDocenti(List.of(docenteRepository.findById(2L).orElseGet(Docente::new)));
        insegnamento2.setBacheca(List.of(messaggioRepository.findById(3L).orElseGet(Messaggio::new), messaggioRepository.findById(4L).orElseGet(Messaggio::new)));
        log.info("Preloading " + insegnamentoRepository.save(insegnamento2));

        var insegnamento3 = new Insegnamento("Fisica 1",
                "Corso di fisica 1",
                9,
                "2024-2025",
                "Lunedì 10:00 - 12:00, Mercoledì 10:00 - 12:00");
        insegnamento3.setDocenti(List.of(docenteRepository.findById(3L).orElseGet(Docente::new)));
        insegnamento3.setBacheca(List.of(messaggioRepository.findById(1L).orElseGet(Messaggio::new), messaggioRepository.findById(2L).orElseGet(Messaggio::new)));
        log.info("Preloading " + insegnamentoRepository.save(insegnamento3));

        var insegnamento4 = new Insegnamento("Fisica 2",
                "Corso di fisica 2",
                9,
                "2024-2025",
                "Lunedì 10:00 - 12:00, Mercoledì 10:00 - 12:00");
        insegnamento4.setDocenti(List.of(docenteRepository.findById(4L).orElseGet(Docente::new)));
        insegnamento4.setBacheca(List.of(messaggioRepository.findById(3L).orElseGet(Messaggio::new), messaggioRepository.findById(4L).orElseGet(Messaggio::new)));
        log.info("Preloading " + insegnamentoRepository.save(insegnamento4));

        var insegnamento5 = new Insegnamento("Programmazione 1",
                "Corso di programmazione 1",
                9,
                "2024-2025",
                "Lunedì 10:00 - 12:00, Mercoledì 10:00 - 12:00");
        insegnamento5.setDocenti(List.of(docenteRepository.findById(1L).orElseGet(Docente::new)));
        insegnamento5.setBacheca(List.of(messaggioRepository.findById(1L).orElseGet(Messaggio::new), messaggioRepository.findById(2L).orElseGet(Messaggio::new)));
        log.info("Preloading " + insegnamentoRepository.save(insegnamento5));

        var insegnamento6 = new Insegnamento("Programmazione 2",
                "Corso di programmazione 2",
                9,
                "2024-2025",
                "Lunedì 10:00 - 12:00, Mercoledì 10:00 - 12:00");
        insegnamento6.setDocenti(List.of(docenteRepository.findById(2L).orElseGet(Docente::new)));
        insegnamento6.setBacheca(List.of(messaggioRepository.findById(3L).orElseGet(Messaggio::new), messaggioRepository.findById(4L).orElseGet(Messaggio::new)));
        log.info("Preloading " + insegnamentoRepository.save(insegnamento6));

    }

    private static void initializeCurricula(CurriculumRepository repository, InsegnamentoRepository insegnamentoRepository, PianoDiStudiRepository pianoDiStudiRepository) {
        log.info("Preloading " + repository.save(new Curriculum(1L,
                "Advanced Software Engineering",
                "Corso di informatica",
                List.of(pianoDiStudiRepository.findById(1L).orElseGet(PianoDiStudi::new))
        )));
        log.info("Preloading " + repository.save(new Curriculum(2L,
                "Advanced Mathematics",
                "Corso di matematica",
                List.of(pianoDiStudiRepository.findById(2L).orElseGet(PianoDiStudi::new))
        )));
        log.info("Preloading " + repository.save(new Curriculum(3L,
                "Advanced Physics",
                "Corso di fisica",
                List.of(pianoDiStudiRepository.findById(3L).orElseGet(PianoDiStudi::new))
        )));
    }

    private static void initializeCorsi(CorsoRepository repository, CurriculumRepository curriculumRepository) {
        log.info("Preloading " + repository.save(new Corso(1L, "Informatica", "Corso di informatica", "2024", Collections.singletonList(curriculumRepository.findById(1L).orElseGet(Curriculum::new)))));
        log.info("Preloading " + repository.save(new Corso(2L, "Matematica", "Corso di matematica", "2024", Collections.singletonList(curriculumRepository.findById(2L).orElseGet(Curriculum::new)))));
        log.info("Preloading " + repository.save(new Corso(3L, "Fisica", "Corso di fisica", "2024", Collections.singletonList(curriculumRepository.findById(3L).orElseGet(Curriculum::new)))));
        log.info("Preloading " + repository.save(new Corso(4L, "Chimica", "Corso di chimica", "2024", null)));
        log.info("Preloading " + repository.save(new Corso(5L, "Biologia", "Corso di biologia", "2024", null)));
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
