package com.beyondrest.SegreteriaVirtualegRPC;

import com.github.javafaker.Faker;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
public class SegreteriaVirtualeClient {
    private static final Logger logger = Logger.getLogger(SegreteriaVirtualeClient.class.getName());
    private final ManagedChannel channel;
    private final SegreteriaVirtualeServiceGrpc.SegreteriaVirtualeServiceBlockingStub blockingStub;
    private final SegreteriaVirtualeServiceGrpc.SegreteriaVirtualeServiceStub asyncStub;

    SegreteriaVirtualeClient(String host, int port) {
        this(Grpc.newChannelBuilderForAddress(host, port, InsecureChannelCredentials.create()).build());
    }

    SegreteriaVirtualeClient(ManagedChannel channel) {
        this.channel = channel;
        this.blockingStub = SegreteriaVirtualeServiceGrpc.newBlockingStub(channel);
        this.asyncStub = SegreteriaVirtualeServiceGrpc.newStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public List<Corso> getCorsi() {
        Empty request = Empty.newBuilder().build();
        CorsiResponse corsi = blockingStub.getCorsi(request);
        logger.info("Corsi: " + corsi.getCorsiList().stream().map(Corso::getNome).reduce((a, b) -> a + ", " + b).orElse(""));
        return corsi.getCorsiList();
    }

    public Corso getCorso(String id) {
        ID request = ID.newBuilder().setId(Integer.parseInt(id)).build();
        CorsoResponse corso = blockingStub.getCorso(request);
        logger.info("Corso: " + corso.getCorso().getNome());
        return corso.getCorso();
    }

    public List<Curriculum> getCurricula(String id) {
        ID request = ID.newBuilder().setId(Integer.parseInt(id)).build();
        CurriculaResponse curricula = blockingStub.getCurricula(request);
        logger.info("Curricula: " + curricula.getCurriculaList().stream().map(Curriculum::getNome).reduce((a, b) -> a + ", " + b).orElse(""));
        return curricula.getCurriculaList();
    }

    public Curriculum getCurriculum(String id1, String id2) {
        DoubleID request = DoubleID.newBuilder().setId1(Integer.parseInt(id1)).setId2(Integer.parseInt(id2)).build();
        CurriculumResponse curriculumResponse = blockingStub.getCurriculum(request);
        logger.info("Curriculum: " + curriculumResponse.getCurriculum().getNome() + " " + curriculumResponse.getCurriculum().getDescrizione());
        return curriculumResponse.getCurriculum();
    }

    public Piano getPiano(String idCorso, String idCurriculum, String anno) {
        GetPianoRequest request = GetPianoRequest.newBuilder().setIdCorso(Integer.parseInt(idCorso)).setIdCurriculum(Integer.parseInt(idCurriculum)).setAnno(anno).build();
        PianoResponse pianoDiStudiResponse = blockingStub.getPiano(request);
        logger.info("Piano di studi: " + pianoDiStudiResponse.getPiano().getAnno() + " " + pianoDiStudiResponse.getPiano().getInsegnamentiList().stream().map(Insegnamento::getNome).reduce((a, b) -> a + ", " + b).orElse(""));
        return pianoDiStudiResponse.getPiano();
    }

    public List<Insegnamento> getInsegnamenti() {
        Empty request = Empty.newBuilder().build();
        InsegnamentiResponse insegnamenti = blockingStub.getInsegnamenti(request);
        logger.info("Insegnamenti: " + insegnamenti.getInsegnamentiList().stream().map(Insegnamento::getNome).reduce((a, b) -> a + ", " + b).orElse(""));
        return insegnamenti.getInsegnamentiList();
    }

    public Insegnamento getInsegnamento(String id) {
        ID request = ID.newBuilder().setId(Integer.parseInt(id)).build();
        InsegnamentoResponse insegnamento = blockingStub.getInsegnamento(request);
        logger.info("Insegnamento: " + insegnamento.getInsegnamento().getNome());
        return insegnamento.getInsegnamento();
    }

    public List<Lezione> getLezioni(String idInsegnamento) {
        ID request = ID.newBuilder().setId(Integer.parseInt(idInsegnamento)).build();
        LezioniResponse lezioni = blockingStub.getLezioni(request);
        logger.info("Lezioni: " + lezioni.getLezioniList().stream().map(Lezione::getDataOra).reduce((a, b) -> a + ", " + b).orElse("") + " " + lezioni.getLezioniList().stream().map(Lezione::getDataOra).reduce((a, b) -> a + ", " + b).orElse(""));
        return lezioni.getLezioniList();
    }

    public List<Messaggio> getMessaggi(String idInsegnamento) {
        ID request = ID.newBuilder().setId(Integer.parseInt(idInsegnamento)).build();
        asyncStub.getMessaggi(request, new StreamObserver<BachecaResponse>() {
            @Override
            public void onNext(BachecaResponse bachecaResponse) {
                logger.info("Messaggi: " + bachecaResponse.getMessaggioList().stream().map(messaggio -> messaggio.getTitolo() + " " + messaggio.getTesto()).reduce((a, b) -> a + ", " + b).orElse(""));
            }

            @Override
            public void onError(Throwable throwable) {
                logger.warning("Error: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                logger.info("Completed");
            }
        });
        return List.of();
    }
    public Messaggio getMessaggio(String idInsegnamento, String idMessaggio) {
        DoubleID request = DoubleID.newBuilder().setId1(Integer.parseInt(idInsegnamento)).setId2(Integer.parseInt(idMessaggio)).build();
        MessaggioResponse messaggio = blockingStub.getMessaggio(request);
        logger.info("Messaggio: " + messaggio.getMessaggio().getTitolo() + " " + messaggio.getMessaggio().getTesto());
        return messaggio.getMessaggio();
    }

    public List<Appello> getAppelli(String idInsegnamento) {
        ID request = ID.newBuilder().setId(Integer.parseInt(idInsegnamento)).build();
        AppelliResponse appelli = blockingStub.getAppelli(request);
        logger.info("Appelli: " + appelli.getAppelliList().stream().map(appello -> appello.getDataOra() + " " + appello.getAula()).reduce((a, b) -> a + ", " + b).orElse(""));
        return appelli.getAppelliList();
    }

    public Appello getAppello(String idInsegnamento, String idAppello) {
        DoubleID request = DoubleID.newBuilder().setId1(Integer.parseInt(idInsegnamento)).setId2(Integer.parseInt(idAppello)).build();
        AppelloResponse appello = blockingStub.getAppello(request);
        logger.info("Appello: " + appello.getAppello().getDataOra() + " " + appello.getAppello().getAula());
        return appello.getAppello();
    }

    public List<Docente> getDocenti() {
        Empty request = Empty.newBuilder().build();
        DocentiResponse docenti = blockingStub.getDocenti(request);
        logger.info("Docenti: " + docenti.getDocentiList().stream().map(docente -> docente.getNome() + " " + docente.getCognome()).reduce((a, b) -> a + ", " + b).orElse(""));
        return docenti.getDocentiList();
    }

    public Docente getDocente(String id) {
        ID request = ID.newBuilder().setId(Integer.parseInt(id)).build();
        DocenteResponse docente = blockingStub.getDocente(request);
        logger.info("Docente: " + docente.getDocente().getNome() + " " + docente.getDocente().getCognome());
        return docente.getDocente();
    }

    public String login(String username, String password) {
        LoginRequest request = LoginRequest.newBuilder().setEmail(username).setPassword(password).build();
        LoginResponse response = blockingStub.login(request);
        return response.getJwtToken();
    }

    public Libretto getLibretto(JwtCredential jwtCredential) {
        Empty request = Empty.newBuilder().build();
        LibrettoResponse response = blockingStub.withCallCredentials(jwtCredential).getLibretto(request);
        logger.info("Libretto: " + response.getLibretto().getVotiList().stream().map(esame -> esame.getInsegnamento() + " " + esame.getVoto()).reduce((a, b) -> a + ", " + b).orElse(""));
        return response.getLibretto();
    }

    public RegistrazioneResponse registrazione(String nome, String cognome, String email, String password) {
        RegistrazioneRequest request = RegistrazioneRequest.newBuilder().setNome(nome).setCognome(cognome).setEmail(email).setPassword(password).build();
        return blockingStub.registrazione(request);
    }

    public AppelloResponse registraAppello(JwtCredential jwtCredential, String idInsegnamento, String idAppello) {
        CreateAppelloRequest request = CreateAppelloRequest.newBuilder().setIdAppello(idAppello).setIdInsegnamento(idInsegnamento).build();
        logger.info("Registrazione appello: " + idInsegnamento + " " + idAppello);
        return blockingStub.withCallCredentials(jwtCredential).registraAppello(request);
    }

    public static void main(String[] args) throws Exception {

        String host = "localhost";
        int port = 9090;

        if (args.length > 0) {
            host = args[0]; // Use the arg as the server host if provided
        }
        if (args.length > 1) {
            port = Integer.parseInt(args[1]); // Use the second argument as the server port if provided
        }

        SegreteriaVirtualeClient client = new SegreteriaVirtualeClient(host, port);

        try {
            var corsi = client.getCorsi();
            var corso = client.getCorso(corsi.get(0).getId());
            var curricula = client.getCurricula(corso.getId());
            var curriculum = client.getCurriculum(corso.getId(), curricula.get(0).getId());
            var piano = client.getPiano(corso.getId(), curriculum.getId(), "2023-2024");
            var insegnamenti = client.getInsegnamenti();
            var insegnamento = client.getInsegnamento(insegnamenti.get(0).getId());
            var lezioni = client.getLezioni(insegnamento.getId());
            var messaggi = client.getMessaggi(insegnamento.getId());
            var appelli = client.getAppelli(insegnamento.getId());
            var appello = client.getAppello(insegnamento.getId(), appelli.get(0).getId());
            var docenti = client.getDocenti();
            var docente = client.getDocente(docenti.get(0).getId());

            var jwtToken = client.login("luca.bianchi@student.univaq.it", "123456");
            JwtCredential jwtCredential = new JwtCredential(jwtToken);
            var libretto = client.getLibretto(jwtCredential);
            var appelloRegistrato = client.registraAppello(jwtCredential, insegnamento.getId(), appelli.get(0).getId());

            Faker faker = new Faker(Locale.ITALIAN);
            var nome = faker.name().name();
            var cognome = faker.name().lastName();
            var email = nome.toLowerCase().replace(" ", ".") + "." + cognome.toLowerCase() + "@student.univaq.it";
            var registrazione = client.registrazione(faker.name().name(), faker.name().lastName(), email, "123456");
            jwtCredential = new JwtCredential(registrazione.getJwtToken());
            libretto = client.getLibretto(jwtCredential);
            while (true) {
                Thread.sleep(1000);
            }
        } finally {
            client.shutdown();
        }
    }
}
