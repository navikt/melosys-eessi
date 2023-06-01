package no.nav.melosys.eessi.service.oppgave;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.identifisering.BucIdentifisertService;
import no.nav.melosys.eessi.identifisering.IdentifiseringKontrollService;
import no.nav.melosys.eessi.identifisering.OppgaveEndretHendelse;
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

    public void behandleOppgaveEndretHendelse(OppgaveEndretHendelse oppgaveEndretHendelse) {
        if (erValidertIdentifiseringsoppgave(oppgaveEndretHendelse)) {
            log.info("Oppgave {} markert som identifisert av ID og Fordeling. Versjon {}. Søker etter tilknyttet RINA-sak", oppgaveEndretHendelse.getId(), oppgaveEndretHendelse.getVersjon());
            bucIdentifiseringOppgRepository.findByOppgaveId(oppgaveEndretHendelse.getId().toString())
                .ifPresentOrElse(
                    b -> kontrollerIdentifiseringOgOppdaterOppgave(b.getRinaSaksnummer(), oppgaveEndretHendelse, b.getVersjon()),
                    () -> log.debug("Finner ikke RINA-sak tilknytning for oppgaveEndretHendelse {}", oppgaveEndretHendelse.getId())
                );
        }
    }


    private boolean erValidertIdentifiseringsoppgave(OppgaveEndretHendelse oppgaveEndretHendelse) {
        return erIdentifiseringsOppgave(oppgaveEndretHendelse) && validerOppgaveStatusOgVersjon(oppgaveEndretHendelse);
    }

    private boolean validerOppgaveStatusOgVersjon(OppgaveEndretHendelse oppgaveEndretHendelse) {
        final var oppgaveId = oppgaveEndretHendelse.getId().toString();
        HentOppgaveDto oppgaveDto = oppgaveService.hentOppgave(oppgaveId);
        if (!oppgaveEndretHendelse.harSammeVersjon(oppgaveDto.getVersjon())) {
            log.info("Kan ikke behandle oppgave endret {}, versjonskonflikt mellom kafkamelding (versjon {}) og oppgave (versjon {}) ", oppgaveId, oppgaveEndretHendelse.getVersjon(), oppgaveDto.getVersjon());
            return false;
        }

        if (!oppgaveDto.erÅpen()) {
            log.info("Kan ikke behandle oppgave endret {}, oppgave er ikke åpen opppgave", oppgaveId);
            return false;
        }

        return true;
    }

    private boolean erIdentifiseringsOppgave(OppgaveEndretHendelse oppgaveEndretHendelse) {
        return "JFR".equals(oppgaveEndretHendelse.getOppgavetype())
            && "4530".equals(oppgaveEndretHendelse.getTildeltEnhetsnr())
            && oppgaveEndretHendelse.harAktørID()
            && GYLDIGE_TEMA.contains(oppgaveEndretHendelse.getTema())
            && oppgaveEndretHendelse.erÅpen()
            && oppgaveEndretHendelse.harMetadataRinasaksnummer();
    }

    private void kontrollerIdentifiseringOgOppdaterOppgave(String rinaSaksnummer,
                                                           OppgaveEndretHendelse oppgaveEndretHendelse,
                                                           int versjon) {
        var kontrollResultat = identifiseringKontrollService.kontrollerIdentifisertPerson(oppgaveEndretHendelse.hentAktørID(), rinaSaksnummer, versjon);
        if (kontrollResultat.erIdentifisert()) {
            log.info("BUC {} identifisert av oppgave {}", rinaSaksnummer, oppgaveEndretHendelse.getId());
            bucIdentifisertService.lagreIdentifisertPerson(rinaSaksnummer, personFasade.hentNorskIdent(oppgaveEndretHendelse.hentAktørID()));
            oppgaveService.ferdigstillOppgave(oppgaveEndretHendelse.getId().toString(), oppgaveEndretHendelse.getVersjon());
        } else {
            log.info("Oppgave {} tilhørende rina-sak {} ikke identifisert. Feilet på: {}", oppgaveEndretHendelse.getId(), rinaSaksnummer, kontrollResultat.getBegrunnelser());
            oppgaveService.flyttOppgaveTilIdOgFordeling(
                oppgaveEndretHendelse.getId().toString(),
                oppgaveEndretHendelse.getVersjon(),
                kontrollResultat.hentFeilIOpplysningerTekst());
            bucIdentifiseringOppgRepository.updateVersjonNumberBy1(
                oppgaveEndretHendelse.getId().toString(),
                rinaSaksnummer);
        }
    }
}
