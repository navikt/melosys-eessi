package no.nav.melosys.eessi.service.sed;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.eux.rina_api.EuxConsumer;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SedJournalføringMigreringService {

    private final SedMottattHendelseRepository sedMottattHendelseRepository;

    private final EuxConsumer euxConsumer;

    String naisClusterName;

    public SedJournalføringMigreringService(SedMottattHendelseRepository sedMottattHendelseRepository,
                                            EuxConsumer euxConsumer) {
        this.sedMottattHendelseRepository = sedMottattHendelseRepository;
        this.euxConsumer = euxConsumer;
        this.naisClusterName = System.getenv().getOrDefault("NAIS_CLUSTER_NAME", "prod-fss");
    }

    // PROD:
    LocalDateTime startTidspunktProd = LocalDateTime.of(2024, 4, 18, 13, 37);
    LocalDateTime sluttTidspunktProd = LocalDateTime.of(2024, 4, 24, 15, 37);
    final String fileNameProd = "migrering-sed-sendt-prod.json";

    // DEV:
    LocalDateTime startTidspunktDev = LocalDateTime.of(2024, 4, 1, 13, 37);
    LocalDateTime sluttTidspunktDev = LocalDateTime.of(2024, 4, 25, 15, 37);
    final String fileNameDev = "migrering-sed-sendt-dev.json";

    private final List<SedMottattMigreringRapportDto> sedMottattMigreringRapportDtoList = new ArrayList<>();
    volatile boolean erKartleggingSedMottattPågående = false;
    private int antallSedMottattHendelser = 0;
    private int antallSedMottattSjekket = 0;

    private final List<SedSendtMigreringRapportDto> sedSendtMigreringRapportDtoList = new ArrayList<>();
    volatile boolean erKartleggingSedSendtPågående = false;
    private int antallSedSendtHendelser = 0;
    private int antallSedSendtSjekket = 0;

    @Async
    @Synchronized
    public void startKartleggingAvSedMottatt() {
        LocalDateTime startTidspunkt = naisClusterName.equals("prod-fss") ? startTidspunktProd : startTidspunktDev;
        LocalDateTime sluttTidspunkt = naisClusterName.equals("prod-fss") ? sluttTidspunktProd : sluttTidspunktDev;

        List<SedMottattHendelse> sedMottattHendelseListe = sedMottattHendelseRepository.findAllByMottattDatoBetween(startTidspunkt, sluttTidspunkt);
        erKartleggingSedMottattPågående = true;
        antallSedMottattHendelser = sedMottattHendelseListe.size();
        antallSedMottattSjekket = 0;

        sedMottattMigreringRapportDtoList.clear();

        log.info("Starter rapportering av sed med vedlegg fra {} til {}. Antall SedMottattHendelser {}", startTidspunkt, sluttTidspunkt, antallSedMottattHendelser);

        for (SedMottattHendelse sedMottattHendelse : sedMottattHendelseListe) {
            if (!erKartleggingSedMottattPågående) {
                break;
            }
            kartleggForSedMottattHendelse(sedMottattHendelse);
        }
        erKartleggingSedMottattPågående = false;
    }

    @Async
    @Synchronized
    public void startKartleggingAvSedSendt() throws IOException, URISyntaxException {
        erKartleggingSedSendtPågående = true;
        antallSedSendtSjekket = 0;
        String fileName = naisClusterName.equals("prod-fss") ? fileNameProd : fileNameDev;

        URI fileUri = (Objects.requireNonNull(getClass().getClassLoader().getResource(fileName))).toURI();
        String content = new String(Files.readAllBytes(Paths.get(fileUri)));

        ObjectMapper objectMapper = new ObjectMapper();
        List<SedSendtJournalføringMigrering> sedSendtJournalføringListe = objectMapper.readValue(content, new TypeReference<>() {
        });
        antallSedSendtHendelser = sedSendtJournalføringListe.size();

        log.info("Starter rapportering av sed sendt med vedlegg fra {}. Antall SedSendtJournalføring {}", fileName, antallSedSendtHendelser);

        for (SedSendtJournalføringMigrering sedSendtJournalføring : sedSendtJournalføringListe) {
            if (!erKartleggingSedSendtPågående) {
                break;
            }
            String rinaSaksnummer = sedSendtJournalføring.rinaSakId();
            String dokumentId = sedSendtJournalføring.rinaDokumentId();

            SedMedVedlegg sedMedVedlegg = euxConsumer.hentSedMedVedlegg(rinaSaksnummer, dokumentId);
            if (!sedMedVedlegg.getVedlegg().isEmpty()) {
                log.info("Fant vedlegg for sed med rinaSaksnummer {}, dokumentId {}", rinaSaksnummer, dokumentId);
                sedSendtMigreringRapportDtoList.add(new SedSendtMigreringRapportDto(rinaSaksnummer, dokumentId));
            }
            antallSedSendtSjekket++;
        }
    }

    public void stoppSedMottattKartlegging() {
        log.info("Stopp rapportering av sed mottatt med vedlegg. Sjekket {} SED. Funnet {} av {} sed med vedlegg.", antallSedMottattSjekket, sedMottattMigreringRapportDtoList.size(), antallSedMottattHendelser);
        erKartleggingSedMottattPågående = false;
    }

    public void stoppSedMendtKartlegging() {
        log.info("Stopp rapportering av sed sendt med vedlegg. Sjekket {} SED. Funnet {} av {} sed med vedlegg.", antallSedSendtSjekket, sedSendtMigreringRapportDtoList.size(), antallSedMottattHendelser);
        erKartleggingSedSendtPågående = false;
    }

    public SedJournalføringMigreringRapportDto hentStatus() {
        return new SedJournalføringMigreringRapportDto(sedMottattMigreringRapportDtoList, sedSendtMigreringRapportDtoList, antallSedMottattHendelser, antallSedMottattSjekket);
    }

    private void kartleggForSedMottattHendelse(SedMottattHendelse sedMottattHendelse) {
        String rinaSaksnummer = sedMottattHendelse.getSedHendelse().getRinaSakId();
        String dokumentId = sedMottattHendelse.getSedHendelse().getRinaDokumentId();
        antallSedMottattSjekket++;

        SedMedVedlegg sedMedVedlegg = euxConsumer.hentSedMedVedlegg(rinaSaksnummer, dokumentId);
        if (!sedMedVedlegg.getVedlegg().isEmpty()) {
            String journalpostId = sedMottattHendelse.getJournalpostId();
            log.info("Fant vedlegg for sed med rinaSaksnummer {}, dokumentId {} og journalpostid {}", rinaSaksnummer, dokumentId, journalpostId);
            sedMottattMigreringRapportDtoList.add(new SedMottattMigreringRapportDto(rinaSaksnummer, dokumentId, journalpostId));
        }
    }
}
