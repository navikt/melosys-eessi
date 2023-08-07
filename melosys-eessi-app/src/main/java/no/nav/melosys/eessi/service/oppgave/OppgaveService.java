package no.nav.melosys.eessi.service.oppgave;

import java.time.LocalDate;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.oppgave.*;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import org.springframework.stereotype.Service;

import static no.nav.melosys.eessi.service.sed.SedTypeTilTemaMapper.temaForSedType;

@Slf4j
@Service
public class OppgaveService {

    private static final String JFR = "JFR";
    private static final String JFR_UT = "JFR_UT";
    private static final String ENHET_ID_FORDELING = "4303";
    private static final String ENHET_MEDLEMSKAP_OG_AVGIFT = "4530";
    private static final String PRIORITET_NORMAL = "NORM";
    private static final String BESKRIVELSE = "EØS - %s\n\rRina-sak - %s\n\rPreutfyllt url for rekvirrering: %s";

    private final OppgaveConsumer oppgaveConsumer;

    public OppgaveService(OppgaveConsumer oppgaveConsumer) {
        this.oppgaveConsumer = oppgaveConsumer;
    }

    public HentOppgaveDto hentOppgave(String oppgaveID) {
        return oppgaveConsumer.hentOppgave(oppgaveID);
    }

    public String opprettOppgaveTilIdOgFordeling(String journalpostID, String sedType, String rinaSaksnummer, String preutfylltLenkeForRekvirering) {
        var oppgaveDto = lagOppgaveDto(journalpostID,sedType,rinaSaksnummer, preutfylltLenkeForRekvirering).build();
        log.info("[EESSI TEST] Oppretter oppgave til ID og fordeling", oppgaveDto, preutfylltLenkeForRekvirering);
        HentOppgaveDto response = oppgaveConsumer.opprettOppgave(oppgaveDto);
        log.info("Oppgave til ID og fordeling opprettet med id {}, rekvirering uuid {}", response.getId(), preutfylltLenkeForRekvirering);
        return response.getId();
    }

    public String opprettOppgaveTilIdOgFordeling(String journalpostID, String sedType, String rinaSaksnummer) {
        var oppgaveDto = lagOppgaveDto(journalpostID, sedType, rinaSaksnummer, "").build();

        HentOppgaveDto response = oppgaveConsumer.opprettOppgave(oppgaveDto);
        log.info("Oppgave til ID og fordeling opprettet med id {}", response.getId());
        return response.getId();
    }

    private OppgaveDto.OppgaveDtoBuilder lagOppgaveDto(String journalpostID, String sedType, String rinaSaksnummer, String preutfylltLenkeForRekvirering){
        return OppgaveDto.builder()
            .aktivDato(LocalDate.now())
            .fristFerdigstillelse(LocalDate.now().plusDays(1))
            .journalpostId(journalpostID)
            .oppgavetype(JFR)
            .prioritet(PRIORITET_NORMAL)
            .tema(temaForSedType(sedType))
            .tildeltEnhetsnr(ENHET_ID_FORDELING)
            .beskrivelse(String.format(BESKRIVELSE, sedType, rinaSaksnummer, preutfylltLenkeForRekvirering))
            .metadata(Map.of(OppgaveMetadataKey.RINA_SAKID, rinaSaksnummer));
    }

    public String opprettUtgåendeJfrOppgave(String journalpostID, SedHendelse sedHendelse, String aktørId, String rinaUrl) {
        var oppgaveDto = OppgaveDto.builder()
                .aktivDato(LocalDate.now())
                .beskrivelse(lagBeskrivelseUtgåendeJfrOppgave(sedHendelse, rinaUrl))
                .fristFerdigstillelse(LocalDate.now().plusDays(1))
                .journalpostId(journalpostID)
                .oppgavetype(JFR_UT)
                .prioritet(PRIORITET_NORMAL)
                .tema("MED") //Utgående vil alltid være MED
                .tildeltEnhetsnr(ENHET_MEDLEMSKAP_OG_AVGIFT)
                .aktoerId(aktørId)
                .build();

        HentOppgaveDto response = oppgaveConsumer.opprettOppgave(oppgaveDto);
        log.info("Utgående journalføringsoppgave opprettet med id {}", response.getId());
        return response.getId();
    }

    private static String lagBeskrivelseUtgåendeJfrOppgave(SedHendelse sedHendelse, String rinaUrl) {
        return String.format("%s, rinaSakId=%s, rinaDokumentId=%s, link=%s",
                sedHendelse.getSedType(), sedHendelse.getRinaSakId(), sedHendelse.getRinaDokumentId(), rinaUrl);
    }

    public void ferdigstillOppgave(String oppgaveId, int versjon) {
        log.info("Ferdigstiller oppgave {}", oppgaveId);

        oppgaveConsumer.oppdaterOppgave(oppgaveId,
                OppgaveOppdateringDto.builder()
                        .id(Integer.parseInt(oppgaveId))
                        .versjon(versjon)
                        .status("FERDIGSTILT")
                        .build()
        );
    }

    public void flyttOppgaveTilIdOgFordeling(String oppgaveID, int versjon, String beskrivelse) {
        var eksisterendeOppgaveBeskrivelse = oppgaveConsumer.hentOppgave(oppgaveID).getBeskrivelse();
        var oppgaveOppdatering = OppgaveOppdateringDto.builder()
            .id(Integer.parseInt(oppgaveID))
            .beskrivelse(eksisterendeOppgaveBeskrivelse, beskrivelse)
            .versjon(versjon)
            .tildeltEnhetsnr(ENHET_ID_FORDELING)
            .tilordnetRessurs("")
            .build();

        oppgaveConsumer.oppdaterOppgave(oppgaveID, oppgaveOppdatering);
    }
}
