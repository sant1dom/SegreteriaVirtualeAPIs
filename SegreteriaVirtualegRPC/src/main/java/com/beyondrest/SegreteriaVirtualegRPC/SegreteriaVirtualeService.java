package com.beyondrest.SegreteriaVirtualegRPC;

import com.beyondrest.SegreteriaVirtualegRPC.corso.CorsoRepository;
import com.beyondrest.SegreteriaVirtualegRPC.docente.DocenteRepository;
import com.beyondrest.SegreteriaVirtualegRPC.insegnamento.InsegnamentoRepository;
import com.beyondrest.SegreteriaVirtualegRPC.studente.StudenteRepository;
import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import net.devh.boot.grpc.server.service.GrpcService;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@GrpcService(interceptors = {JwtServerInterceptor.class})
public class SegreteriaVirtualeService extends SegreteriaVirtualeServiceGrpc.SegreteriaVirtualeServiceImplBase {
    private final CorsoRepository corsoRepository;
    private final InsegnamentoRepository insegnamentoRepository;
    private final DocenteRepository docenteRepository;
    private final StudenteRepository studenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    SegreteriaVirtualeService(CorsoRepository corsoRepository, InsegnamentoRepository insegnamentoRepository, DocenteRepository docenteRepository, StudenteRepository studenteRepository, PasswordEncoder passwordEncoder) {
        this.corsoRepository = corsoRepository;
        this.insegnamentoRepository = insegnamentoRepository;
        this.docenteRepository = docenteRepository;
        this.studenteRepository = studenteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void getCorsi(Empty request, StreamObserver<CorsiResponse> responseObserver) {
        List<com.beyondrest.SegreteriaVirtualegRPC.corso.Corso> corsi = corsoRepository.findAll();
        var corsiResponse = corsi.stream().map(corso -> Corso.newBuilder().setId(String.valueOf(corso.getId()))
                .setNome(corso.getNome())
                .setAnno(corso.getAnno())
                .setDescrizione(corso.getDescrizione())
                .build()).toList();
        responseObserver.onNext(CorsiResponse.newBuilder().addAllCorsi(corsiResponse).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getCorso(ID request, StreamObserver<CorsoResponse> responseObserver) {
        com.beyondrest.SegreteriaVirtualegRPC.corso.Corso corso = corsoRepository.findById((long) request.getId()).orElseThrow();
        var corsoResponse = Corso.newBuilder().setId(String.valueOf(corso.getId()))
                .setNome(corso.getNome())
                .setAnno(corso.getAnno())
                .setDescrizione(corso.getDescrizione())
                .build();
        responseObserver.onNext(CorsoResponse.newBuilder().setCorso(corsoResponse).build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void getCurricula(ID request, StreamObserver<CurriculaResponse> responseObserver) {
        com.beyondrest.SegreteriaVirtualegRPC.corso.Corso corso = corsoRepository.findById((long) request.getId()).orElseThrow();
        Hibernate.initialize(corso.getCurricula());
        var curriculaResponse = CurriculaResponse.newBuilder().addAllCurricula(corso.getCurricula().stream().map(curriculum -> Curriculum.newBuilder()
                .setId(String.valueOf(curriculum.getId()))
                .setNome(curriculum.getNome())
                .setDescrizione(curriculum.getDescrizione())
                .build()).toList()).build();
        responseObserver.onNext(curriculaResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void getCurriculum(DoubleID request, StreamObserver<CurriculumResponse> responseObserver) {
        com.beyondrest.SegreteriaVirtualegRPC.corso.Corso corso = corsoRepository.findByIdWithCurricula((long) request.getId1()).orElseThrow();
        var curriculumResponse = CurriculumResponse.newBuilder().setCurriculum(corso.getCurricula().stream().filter(c -> c.getId() == request.getId2()).map(curriculum -> Curriculum.newBuilder()
                .setId(String.valueOf(curriculum.getId()))
                .setNome(curriculum.getNome())
                .setDescrizione(curriculum.getDescrizione())
                .build()).toList().get(0)).build();
        responseObserver.onNext(curriculumResponse);
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void getPiano(GetPianoRequest request, StreamObserver<PianoResponse> responseObserver) {
        var piano = corsoRepository.findByIdWithCurriculaAndPianiDiStudi((long) request.getIdCorso())
                .orElseThrow()
                .getCurricula()
                .stream()
                .filter(curriculum -> curriculum.getId().equals((long) request.getIdCurriculum()))
                .findFirst()
                .orElseThrow()
                .getPianoDiStudi()
                .stream()
                .filter(pianoDiStudi -> pianoDiStudi.getAnno().equals(request.getAnno()));
        var pianoResponse = PianoResponse.newBuilder().setPiano(piano.map(pianoDiStudi -> Piano.newBuilder()
                .setAnno(pianoDiStudi.getAnno())
                .addAllInsegnamenti(pianoDiStudi.getInsegnamenti().stream().map(insegnamento -> Insegnamento.newBuilder()
                        .setId(String.valueOf(insegnamento.getId()))
                        .setNome(insegnamento.getNome())
                        .setDescrizione(insegnamento.getDescrizione())
                        .build()).toList())
                .build()).toList().get(0)).build();
        responseObserver.onNext(pianoResponse);
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void getInsegnamenti(Empty request, StreamObserver<InsegnamentiResponse> responseObserver) {
        var insegnamenti = insegnamentoRepository.findAll();
        var insegnamentiResponse = InsegnamentiResponse.newBuilder()
                .addAllInsegnamenti(
                        insegnamenti.stream().map(insegnamento -> {
                            var insegnamentoBuilder = Insegnamento.newBuilder()
                                    .setId(String.valueOf(insegnamento.getId()))
                                    .setNome(insegnamento.getNome())
                                    .setDescrizione(insegnamento.getDescrizione());

                            insegnamento.getDocenti().forEach(doc -> {
                                insegnamentoBuilder.addDocenti(String.valueOf(doc.getId()));
                            });

                            return insegnamentoBuilder.build();
                        }).toList()
                ).build();
        responseObserver.onNext(insegnamentiResponse);
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void getInsegnamento(ID request, StreamObserver<InsegnamentoResponse> responseObserver) {
        var insegnamento = insegnamentoRepository.findById((long) request.getId()).orElseThrow();
        var insegnamentoResponse = Insegnamento.newBuilder()
                .setId(String.valueOf(insegnamento.getId()))
                .setNome(insegnamento.getNome())
                .setDescrizione(insegnamento.getDescrizione());
        insegnamento.getDocenti().forEach(doc -> {
            insegnamentoResponse.addDocenti(String.valueOf(doc.getId()));
        });
        insegnamentoResponse.build();
        responseObserver.onNext(InsegnamentoResponse.newBuilder().setInsegnamento(insegnamentoResponse).build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void getLezioni(ID request, StreamObserver<LezioniResponse> responseObserver) {
        var insegnamento = insegnamentoRepository.findById((long) request.getId()).orElseThrow();
        var lezioniResponse = LezioniResponse.newBuilder()
                .addAllLezioni(
                        insegnamento.getLezioni().stream().map(lezione -> Lezione.newBuilder()
                                .setId(String.valueOf(lezione.getId()))
                                .setInsegnamento(String.valueOf(insegnamento.getId()))
                                .setDataOra(lezione.getData().toString())
                                .setDiario(lezione.getDiarioDellaLezione())
                                .build()).toList()
                ).build();
        responseObserver.onNext(lezioniResponse);
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void getLezione(DoubleID request, StreamObserver<LezioneResponse> responseObserver) {
        var insegnamento = insegnamentoRepository.findById((long) request.getId1()).orElseThrow();
        var lezione = insegnamento.getLezioni().stream().filter(l -> l.getId() == request.getId2()).findFirst().orElseThrow();
        var lezioneResponse = Lezione.newBuilder()
                .setId(String.valueOf(lezione.getId()))
                .setInsegnamento(String.valueOf(insegnamento.getId()))
                .setDataOra(lezione.getData().toString())
                .setDiario(lezione.getDiarioDellaLezione())
                .build();
        responseObserver.onNext(LezioneResponse.newBuilder().setLezione(lezioneResponse).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getMessaggi(ID request, StreamObserver<BachecaResponse> responseObserver) {
        long id = request.getId();
        final long[] lastMessageId = {0}; // Traccia dell'ultimo messaggio inviato

        scheduler.scheduleAtFixedRate(() -> {
            try {
                var messaggi = insegnamentoRepository.findById(id)
                        .orElseThrow()
                        .getBacheca();

                List<com.beyondrest.SegreteriaVirtualegRPC.messaggio.Messaggio> nuoviMessaggi = messaggi.stream()
                        .filter(messaggio -> messaggio.getId() > lastMessageId[0])
                        .toList();

                if (!nuoviMessaggi.isEmpty()) {
                    lastMessageId[0] = nuoviMessaggi.get(nuoviMessaggi.size() - 1).getId();
                    var messaggiResponse = BachecaResponse.newBuilder()
                            .addAllMessaggio(
                                    nuoviMessaggi.stream().map(messaggio -> Messaggio.newBuilder()
                                            .setId(String.valueOf(messaggio.getId()))
                                            .setTesto(messaggio.getTesto())
                                            .setDataOra(messaggio.getData().toString())
                                            .setAutore(messaggio.getAutore().getId().toString())
                                            .build()).toList()
                            ).build();

                    responseObserver.onNext(messaggiResponse);
                }
            } catch (Exception e) {
                responseObserver.onError(new RuntimeException("Errore durante il recupero dei messaggi", e));
            }
        }, 0, 10, TimeUnit.SECONDS); // Esegui ogni 10 secondi
        responseObserver.onCompleted();
    }

    @Override
    public void getMessaggio(DoubleID request, StreamObserver<MessaggioResponse> responseObserver) {
        var messaggio = insegnamentoRepository.findById((long) request.getId1()).orElseThrow().getBacheca().stream().filter(m -> m.getId() == request.getId2()).findFirst().orElseThrow();
        var messaggioResponse = Messaggio.newBuilder()
                .setId(String.valueOf(messaggio.getId()))
                .setTesto(messaggio.getTesto())
                .setDataOra(messaggio.getData().toString())
                .setAutore(messaggio.getAutore().getId().toString())
                .build();
        responseObserver.onNext(MessaggioResponse.newBuilder().setMessaggio(messaggioResponse).build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void getAppelli(ID request, StreamObserver<AppelliResponse> responseObserver) {
        var appelli = insegnamentoRepository.findById((long) request.getId()).orElseThrow().getAppelli();
        var appelliResponse = AppelliResponse.newBuilder()
                .addAllAppelli(
                        appelli.stream().map(appello -> {
                            var appelloBuilder = Appello.newBuilder()
                                    .setId(String.valueOf(appello.getId()))
                                    .setDataOra(appello.getData().toString())
                                    .setAula(appello.getAula())
                                    .setInsegnamento(String.valueOf(appello.getInsegnamento().getId()));
                            appello.getIscritti().forEach(iscritto -> {
                                appelloBuilder.addIscritti(String.valueOf(iscritto.getId()));
                            });
                            return appelloBuilder.build();
                        }).toList()).build();
        responseObserver.onNext(appelliResponse);
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void getAppello(DoubleID request, StreamObserver<AppelloResponse> responseObserver) {
        var insegnamento = insegnamentoRepository.findById((long) request.getId1()).orElseThrow();
        var appello = insegnamento.getAppelli().stream().filter(a -> a.getId() == request.getId2()).findFirst().orElseThrow();
        var appelloResponse = Appello.newBuilder()
                .setId(String.valueOf(appello.getId()))
                .setDataOra(appello.getData().toString())
                .setAula(appello.getAula())
                .setInsegnamento(String.valueOf(appello.getInsegnamento().getId()));
        appello.getIscritti().forEach(iscritto -> {
            appelloResponse.addIscritti(String.valueOf(iscritto.getId()));
        });
        appelloResponse.build();
        responseObserver.onNext(AppelloResponse.newBuilder().setAppello(appelloResponse).build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void registraAppello(CreateAppelloRequest request, StreamObserver<AppelloResponse> responseObserver) {
        var client = Constant.CLIENT_ID_CONTEXT_KEY;
        var studente = studenteRepository.findById(Long.parseLong(client.get())).orElseThrow();
        var appello = insegnamentoRepository.findById(Long.valueOf(request.getIdInsegnamento())).orElseThrow().getAppelli().stream().filter(a -> Objects.equals(a.getId(), Long.valueOf(request.getIdAppello()))).findFirst().orElseThrow();
        appello.getIscritti().add(studente);
        var appelloResponse = Appello.newBuilder()
                .setId(String.valueOf(appello.getId()))
                .setDataOra(appello.getData().toString())
                .setAula(appello.getAula())
                .setInsegnamento(String.valueOf(appello.getInsegnamento().getId()));
        appello.getIscritti().forEach(iscritto -> {
            appelloResponse.addIscritti(String.valueOf(iscritto.getId()));
        });
        appelloResponse.build();
        responseObserver.onNext(AppelloResponse.newBuilder().setAppello(appelloResponse).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getDocenti(Empty request, StreamObserver<DocentiResponse> responseObserver) {
        var docenti = docenteRepository.findAll();
        var docentiResponse = DocentiResponse.newBuilder()
                .addAllDocenti(
                        docenti.stream().map(docente -> Docente.newBuilder()
                                .setId(String.valueOf(docente.getId()))
                                .setNome(docente.getNome())
                                .setCognome(docente.getCognome())
                                .setEmail(docente.getEmail())
                                .build()).toList()
                ).build();
        responseObserver.onNext(docentiResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void getDocente(ID request, StreamObserver<DocenteResponse> responseObserver) {
        var docente = docenteRepository.findById((long) request.getId()).orElseThrow();
        var docenteResponse = Docente.newBuilder()
                .setId(String.valueOf(docente.getId()))
                .setNome(docente.getNome())
                .setCognome(docente.getCognome())
                .setEmail(docente.getEmail())
                .build();
        responseObserver.onNext(DocenteResponse.newBuilder().setDocente(docenteResponse).build());
        responseObserver.onCompleted();
    }

    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        var studente = studenteRepository.findByEmailIgnoreCase(request.getEmail());
        if (studente.isPresent() && passwordEncoder.matches(request.getPassword(), studente.get().getPassword())) {
            responseObserver.onNext(LoginResponse.newBuilder().setJwtToken(JwtCredential.generateJwt(String.valueOf(studente.get().getId()))).build());
        } else {
            responseObserver.onNext(LoginResponse.newBuilder().setJwtToken("").build());
        }
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void getLibretto(Empty request, StreamObserver<LibrettoResponse> responseObserver) {
        var client = Constant.CLIENT_ID_CONTEXT_KEY;
        var studente = studenteRepository.findById(Long.parseLong(client.get())).orElseThrow();
        var libretto = studente.getPianoDiStudiPrivato();
        LibrettoResponse librettoResponse;
        if (libretto != null) {
            librettoResponse = LibrettoResponse.newBuilder()
                    .setLibretto(Libretto.newBuilder()
                            .addAllVoti(libretto.getVoti().stream().map(voto -> Voto.newBuilder()
                                    .setId(String.valueOf(voto.getId()))
                                    .setInsegnamento(String.valueOf(voto.getInsegnamento().getId()))
                                    .setVoto(voto.getVotazione())
                                    .setDataOra(voto.getData().toString())
                                    .build()).toList())
                            .build())
                    .build();
        } else {
            librettoResponse = LibrettoResponse.newBuilder().setLibretto(Libretto.newBuilder().build()).build();
        }
        responseObserver.onNext(librettoResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void registrazione(RegistrazioneRequest request, StreamObserver<RegistrazioneResponse> responseObserver) {
        var studente = studenteRepository.findByEmail(request.getEmail());
        if (studente.isEmpty()) {
            var newStudente = studenteRepository.save(new com.beyondrest.SegreteriaVirtualegRPC.studente.Studente(request.getNome(), request.getCognome(), request.getEmail(), request.getNome()+"."+request.getCognome(), request.getPassword()));
            responseObserver.onNext(RegistrazioneResponse.newBuilder().setJwtToken(JwtCredential.generateJwt(String.valueOf(newStudente.getId()))).build());
        } else {
            throw new RuntimeException("Studente già registrato");
        }
        responseObserver.onCompleted();
    }
}
