package no.nav.melosys.eessi.service.sed;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

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

    // DEV:
    LocalDateTime startTidspunktDev = LocalDateTime.of(2024, 4, 1, 13, 37);
    LocalDateTime sluttTidspunktDev = LocalDateTime.of(2024, 4, 25, 15, 37);

    boolean erKartleggingPågående = false;
    private final HashMap<String, String> rinasaksnummerTilDokumentId = new HashMap<>();
    private final List<SedMottattMigreringRapportDto> sedMottattMigreringRapportDtoList = List.of();
    private int antallSedMottattHendelser = 0;
    private int antallSedSjekket = 0;

    @Async
    @Synchronized
    public void startKartleggingAvSedMottatt() {
        LocalDateTime startTidspunkt = naisClusterName.equals("prod-fss") ? startTidspunktProd : startTidspunktDev;
        LocalDateTime sluttTidspunkt = naisClusterName.equals("prod-fss") ? sluttTidspunktProd : sluttTidspunktDev;

        List<SedMottattHendelse> sedMottattHendelseListe = sedMottattHendelseRepository.findAllByMottattDatoBetween(startTidspunkt, sluttTidspunkt);
        erKartleggingPågående = true;
        antallSedMottattHendelser = sedMottattHendelseListe.size();
        antallSedSjekket = 0;

        sedMottattMigreringRapportDtoList.clear();

        log.info("Starter rapportering av sed med vedlegg fra {} til {}. Antall SedMottattHendelser {}", startTidspunkt, sluttTidspunkt, antallSedMottattHendelser);

        for (SedMottattHendelse sedMottattHendelse : sedMottattHendelseListe) {
            if (!erKartleggingPågående) {
                break;
            }
            String rinaSaksnummer = sedMottattHendelse.getSedHendelse().getRinaSakId();
            String dokumentId = sedMottattHendelse.getSedHendelse().getRinaDokumentId();
            antallSedSjekket++;

            SedMedVedlegg sedMedVedlegg = euxConsumer.hentSedMedVedlegg(rinaSaksnummer, dokumentId);
            if (!sedMedVedlegg.getVedlegg().isEmpty()) {
                String journalpostId = sedMottattHendelse.getJournalpostId();
                log.info("Fant vedlegg for sed med rinaSaksnummer {}, dokumentId {} og journalpostid {}", rinaSaksnummer, dokumentId, journalpostId);
                sedMottattMigreringRapportDtoList.add(new SedMottattMigreringRapportDto(rinaSaksnummer, dokumentId, journalpostId));
            }
        }
        erKartleggingPågående = false;
    }

    public void stoppKartlegging() {
        log.info("Stopp rapportering av sed med vedlegg. Sjekket {} SED. Funnet {} av {} sed med vedlegg.", antallSedSjekket, rinasaksnummerTilDokumentId.size(), antallSedMottattHendelser);
        erKartleggingPågående = false;
    }

    public SedJournalføringMigreringRapportDto hentStatus() {
        return new SedJournalføringMigreringRapportDto(sedMottattMigreringRapportDtoList, antallSedMottattHendelser, antallSedSjekket);
    }
}
