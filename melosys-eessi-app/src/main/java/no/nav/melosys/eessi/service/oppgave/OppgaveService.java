// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.service.oppgave;

import java.time.LocalDate;
import java.util.Map;

import no.nav.melosys.eessi.integration.oppgave.*;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import static no.nav.melosys.eessi.service.sed.SedTypeTilTemaMapper.temaForSedType;

@Service
public class OppgaveService {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OppgaveService.class);
    private static final String JFR = "JFR";
    private static final String JFR_UT = "JFR_UT";
    private static final String ENHET_ID_FORDELING = "4303";
    private static final String ENHET_MEDLEMSKAP_OG_AVGIFT = "4530";
    private static final String PRIORITET_NORMAL = "NORM";
    private static final String BESKRIVELSE_MED_REKVIRERING_URL = "Url for rekvirering: %s\n\rEØS - %s\n\rRina-sak - %s";
    private static final String BESKRIVELSE = "EØS - %s\n\rRina-sak - %s";
    private final OppgaveConsumer oppgaveConsumer;

    public OppgaveService(OppgaveConsumer oppgaveConsumer) {
        this.oppgaveConsumer = oppgaveConsumer;
    }

    public HentOppgaveDto hentOppgave(String oppgaveID) {
        return oppgaveConsumer.hentOppgave(oppgaveID);
    }

    public String opprettOppgaveTilIdOgFordeling(String journalpostID, String sedType, String rinaSaksnummer, String lenkeForRekvirering) {
        var oppgaveDto = lagOppgaveDto(journalpostID, sedType, rinaSaksnummer, lenkeForRekvirering).build();
        HentOppgaveDto response = oppgaveConsumer.opprettOppgave(oppgaveDto);
        log.info("Oppgave til ID og fordeling opprettet med id {}, rekvirering uuid {}", response.getId(), lenkeForRekvirering);
        return response.getId();
    }

    public String opprettOppgaveTilIdOgFordeling(String journalpostID, String sedType, String rinaSaksnummer) {
        var oppgaveDto = lagOppgaveDto(journalpostID, sedType, rinaSaksnummer, null).build();
        HentOppgaveDto response = oppgaveConsumer.opprettOppgave(oppgaveDto);
        log.info("Oppgave til ID og fordeling opprettet med id {}", response.getId());
        return response.getId();
    }

    private OppgaveDto.OppgaveDtoBuilder lagOppgaveDto(String journalpostID, String sedType, String rinaSaksnummer, String lenkeForRekvirering) {
        var oppgaveDto = OppgaveDto.builder().aktivDato(LocalDate.now()).fristFerdigstillelse(LocalDate.now().plusDays(1)).journalpostId(journalpostID).oppgavetype(JFR).prioritet(PRIORITET_NORMAL).tema(temaForSedType(sedType)).tildeltEnhetsnr(ENHET_ID_FORDELING).metadata(Map.of(OppgaveMetadataKey.RINA_SAKID, rinaSaksnummer));
        if (!StringUtils.isEmpty(lenkeForRekvirering)) {
            oppgaveDto.beskrivelse(String.format(BESKRIVELSE_MED_REKVIRERING_URL, lenkeForRekvirering, sedType, rinaSaksnummer));
        } else {
            oppgaveDto.beskrivelse(String.format(BESKRIVELSE, sedType, rinaSaksnummer));
        }
        return oppgaveDto;
    }

    public String opprettUtgåendeJfrOppgave(String journalpostID, SedHendelse sedHendelse, String aktørId, String rinaUrl) {
        var oppgaveDto =  //Utgående vil alltid være MED
            OppgaveDto.builder().aktivDato(LocalDate.now()).beskrivelse(lagBeskrivelseUtgåendeJfrOppgave(sedHendelse, rinaUrl)).fristFerdigstillelse(LocalDate.now().plusDays(1)).journalpostId(journalpostID).oppgavetype(JFR_UT).prioritet(PRIORITET_NORMAL).tema("MED").tildeltEnhetsnr(ENHET_MEDLEMSKAP_OG_AVGIFT).aktoerId(aktørId).build();
        HentOppgaveDto response = oppgaveConsumer.opprettOppgave(oppgaveDto);
        log.info("Utgående journalføringsoppgave opprettet med id {}", response.getId());
        return response.getId();
    }

    private static String lagBeskrivelseUtgåendeJfrOppgave(SedHendelse sedHendelse, String rinaUrl) {
        return String.format("%s, rinaSakId=%s, rinaDokumentId=%s, link=%s", sedHendelse.getSedType(), sedHendelse.getRinaSakId(), sedHendelse.getRinaDokumentId(), rinaUrl);
    }

    public void ferdigstillOppgave(String oppgaveId, int versjon) {
        log.info("Ferdigstiller oppgave {}", oppgaveId);
        oppgaveConsumer.oppdaterOppgave(oppgaveId, OppgaveOppdateringDto.builder().id(Integer.parseInt(oppgaveId)).versjon(versjon).status("FERDIGSTILT").build());
    }

    public void flyttOppgaveTilIdOgFordeling(String oppgaveID, int versjon, String beskrivelse) {
        var eksisterendeOppgaveBeskrivelse = oppgaveConsumer.hentOppgave(oppgaveID).getBeskrivelse();
        var oppgaveOppdatering = OppgaveOppdateringDto.builder().id(Integer.parseInt(oppgaveID)).beskrivelse(eksisterendeOppgaveBeskrivelse, beskrivelse).versjon(versjon).tildeltEnhetsnr(ENHET_ID_FORDELING).tilordnetRessurs("").build();
        oppgaveConsumer.oppdaterOppgave(oppgaveID, oppgaveOppdatering);
    }
}
