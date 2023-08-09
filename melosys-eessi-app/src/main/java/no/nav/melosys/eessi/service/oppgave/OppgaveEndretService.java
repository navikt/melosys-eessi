package no.nav.melosys.eessi.service.oppgave;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.identifisering.BucIdentifisertService;
import no.nav.melosys.eessi.identifisering.IdentifiseringKontrollService;
import no.nav.melosys.eessi.identifisering.OppgaveKafkaAivenRecord;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.integration.oppgave.HentOppgaveDto;
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class OppgaveEndretService {

    private final BucIdentifisertService bucIdentifisertService;
    private final BucIdentifiseringOppgRepository bucIdentifiseringOppgRepository;
    private final OppgaveService oppgaveService;
    private final IdentifiseringKontrollService identifiseringKontrollService;
    private final PersonFasade personFasade;

    private static final Collection<String> GYLDIGE_TEMA = Set.of("MED", "UFM");

    public void behandleOppgaveEndretHendelse(OppgaveKafkaAivenRecord oppgaveEndretHendelse) {
        if (erValidertIdentifiseringsoppgave(oppgaveEndretHendelse)) {
            log.info("Oppgave {} markert som identifisert av ID og Fordeling. Versjon {}. Søker etter tilknyttet RINA-sak", oppgaveEndretHendelse.oppgave().oppgaveId(), oppgaveEndretHendelse.oppgave().versjon());
            bucIdentifiseringOppgRepository.findByOppgaveId(oppgaveEndretHendelse.oppgave().oppgaveId().toString())
                .ifPresentOrElse(
                    b -> kontrollerIdentifiseringOgOppdaterOppgave(b.getRinaSaksnummer(), oppgaveEndretHendelse, b.getVersjon()),
                    () -> log.debug("Finner ikke RINA-sak tilknytning for oppgaveEndretHendelse {}", oppgaveEndretHendelse.oppgave().oppgaveId())
                );
        }
    }


    private boolean erValidertIdentifiseringsoppgave(OppgaveKafkaAivenRecord oppgaveEndretHendelse) {
        return erIdentifiseringsOppgave(oppgaveEndretHendelse) && validerOppgaveStatusOgVersjon(oppgaveEndretHendelse);
    }

    private boolean validerOppgaveStatusOgVersjon(OppgaveKafkaAivenRecord oppgaveEndretHendelse) {
        final var oppgaveId = oppgaveEndretHendelse.oppgave().oppgaveId().toString();
        HentOppgaveDto oppgaveDto = oppgaveService.hentOppgave(oppgaveId);
        if (!oppgaveEndretHendelse.harSammeVersjon(oppgaveDto.getVersjon())) {
            log.info("Kan ikke behandle oppgave endret {}, versjonskonflikt mellom kafkamelding (versjon {}) og oppgave (versjon {}) ", oppgaveId, oppgaveEndretHendelse.oppgave().versjon(), oppgaveDto.getVersjon());
            return false;
        }

        if (!oppgaveDto.erÅpen()) {
            log.info("Kan ikke behandle oppgave endret {}, oppgave er ikke åpen opppgave", oppgaveId);
            return false;
        }

        return true;
    }

    private boolean erIdentifiseringsOppgave(OppgaveKafkaAivenRecord oppgaveEndretHendelse) {
        return "JFR".equals(oppgaveEndretHendelse.oppgave().kategorisering().oppgavetype())
            && "4530".equals(oppgaveEndretHendelse.oppgave().tilordning().enhetsnr())
            && oppgaveEndretHendelse.harAktørID()
            && GYLDIGE_TEMA.contains(oppgaveEndretHendelse.oppgave().kategorisering().tema());
        // && oppgaveEndretHendelse.harMetadataRinasaksnummer(); // TODO: Hva gjør vi med harMetadataRinasaksnummer?
    }

    private void kontrollerIdentifiseringOgOppdaterOppgave(String rinaSaksnummer,
                                                           OppgaveKafkaAivenRecord oppgaveEndretHendelse,
                                                           int versjon) {
        var kontrollResultat = identifiseringKontrollService.kontrollerIdentifisertPerson(oppgaveEndretHendelse.hentAktørID(), rinaSaksnummer, versjon);
        if (kontrollResultat.erIdentifisert()) {
            log.info("BUC {} identifisert av oppgave {}", rinaSaksnummer, oppgaveEndretHendelse.oppgave().oppgaveId());
            bucIdentifisertService.lagreIdentifisertPerson(rinaSaksnummer, personFasade.hentNorskIdent(oppgaveEndretHendelse.hentAktørID()));
            oppgaveService.ferdigstillOppgave(oppgaveEndretHendelse.oppgave().oppgaveId().toString(), oppgaveEndretHendelse.oppgave().versjon());
        } else {
            log.info("Oppgave {} tilhørende rina-sak {} ikke identifisert. Feilet på: {}", oppgaveEndretHendelse.oppgave().oppgaveId(), rinaSaksnummer, kontrollResultat.getBegrunnelser());
            oppgaveService.flyttOppgaveTilIdOgFordeling(
                oppgaveEndretHendelse.oppgave().oppgaveId().toString(),
                oppgaveEndretHendelse.oppgave().versjon(),
                kontrollResultat.hentFeilIOpplysningerTekst());
            bucIdentifiseringOppgRepository.updateVersjonNumberBy1(
                oppgaveEndretHendelse.oppgave().oppgaveId().toString(),
                rinaSaksnummer);
        }
    }
}
