package no.nav.melosys.eessi.service.oppgave;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.identifisering.BucIdentifisertService;
import no.nav.melosys.eessi.identifisering.IdentifiseringKontrollService;
import no.nav.melosys.eessi.identifisering.OppgaveEndretHendelseGammel;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.integration.oppgave.HentOppgaveDto;
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class OppgaveEndretServiceGammel {

    private final BucIdentifisertService bucIdentifisertService;
    private final BucIdentifiseringOppgRepository bucIdentifiseringOppgRepository;
    private final OppgaveService oppgaveService;
    private final IdentifiseringKontrollService identifiseringKontrollService;
    private final PersonFasade personFasade;

    private static final Collection<String> GYLDIGE_TEMA = Set.of("MED", "UFM");

    public void behandleOppgaveEndretHendelse(OppgaveEndretHendelseGammel oppgaveEndretHendelseGammel) {
        if (erValidertIdentifiseringsoppgave(oppgaveEndretHendelseGammel)) {
            log.info("Oppgave {} markert som identifisert av ID og Fordeling. Versjon {}. Søker etter tilknyttet RINA-sak", oppgaveEndretHendelseGammel.getId(), oppgaveEndretHendelseGammel.getVersjon());
            bucIdentifiseringOppgRepository.findByOppgaveId(oppgaveEndretHendelseGammel.getId().toString())
                .ifPresentOrElse(
                    b -> kontrollerIdentifiseringOgOppdaterOppgave(b.getRinaSaksnummer(), oppgaveEndretHendelseGammel, b.getVersjon()),
                    () -> log.debug("Finner ikke RINA-sak tilknytning for oppgaveEndretHendelse {}", oppgaveEndretHendelseGammel.getId())
                );
        }
    }


    private boolean erValidertIdentifiseringsoppgave(OppgaveEndretHendelseGammel oppgaveEndretHendelseGammel) {
        return erIdentifiseringsOppgave(oppgaveEndretHendelseGammel) && validerOppgaveStatusOgVersjon(oppgaveEndretHendelseGammel);
    }

    private boolean validerOppgaveStatusOgVersjon(OppgaveEndretHendelseGammel oppgaveEndretHendelseGammel) {
        final var oppgaveId = oppgaveEndretHendelseGammel.getId().toString();
        HentOppgaveDto oppgaveDto = oppgaveService.hentOppgave(oppgaveId);
        if (!oppgaveEndretHendelseGammel.harSammeVersjon(oppgaveDto.getVersjon())) {
            log.info("Kan ikke behandle oppgave endret {}, versjonskonflikt mellom kafkamelding (versjon {}) og oppgave (versjon {}) ", oppgaveId, oppgaveEndretHendelseGammel.getVersjon(), oppgaveDto.getVersjon());
            return false;
        }

        if (!oppgaveDto.erÅpen()) {
            log.info("Kan ikke behandle oppgave endret {}, oppgave er ikke åpen opppgave", oppgaveId);
            return false;
        }

        return true;
    }

    private boolean erIdentifiseringsOppgave(OppgaveEndretHendelseGammel oppgaveEndretHendelseGammel) {
        return "JFR".equals(oppgaveEndretHendelseGammel.getOppgavetype())
            && "4530".equals(oppgaveEndretHendelseGammel.getTildeltEnhetsnr())
            && oppgaveEndretHendelseGammel.harAktørID()
            && GYLDIGE_TEMA.contains(oppgaveEndretHendelseGammel.getTema())
            && oppgaveEndretHendelseGammel.erÅpen()
            && oppgaveEndretHendelseGammel.harMetadataRinasaksnummer();
    }

    private void kontrollerIdentifiseringOgOppdaterOppgave(String rinaSaksnummer,
                                                           OppgaveEndretHendelseGammel oppgaveEndretHendelseGammel,
                                                           int versjon) {
        var kontrollResultat = identifiseringKontrollService.kontrollerIdentifisertPerson(oppgaveEndretHendelseGammel.hentAktørID(), rinaSaksnummer, versjon);
        if (kontrollResultat.erIdentifisert()) {
            log.info("BUC {} identifisert av oppgave {}", rinaSaksnummer, oppgaveEndretHendelseGammel.getId());
            bucIdentifisertService.lagreIdentifisertPerson(rinaSaksnummer, personFasade.hentNorskIdent(oppgaveEndretHendelseGammel.hentAktørID()));
            oppgaveService.ferdigstillOppgave(oppgaveEndretHendelseGammel.getId().toString(), oppgaveEndretHendelseGammel.getVersjon());
        } else {
            log.info("Oppgave {} tilhørende rina-sak {} ikke identifisert. Feilet på: {}", oppgaveEndretHendelseGammel.getId(), rinaSaksnummer, kontrollResultat.getBegrunnelser());
            oppgaveService.flyttOppgaveTilIdOgFordeling(
                oppgaveEndretHendelseGammel.getId().toString(),
                oppgaveEndretHendelseGammel.getVersjon(),
                kontrollResultat.hentFeilIOpplysningerTekst());
            bucIdentifiseringOppgRepository.updateVersjonNumberBy1(
                oppgaveEndretHendelseGammel.getId().toString(),
                rinaSaksnummer);
        }
    }
}
