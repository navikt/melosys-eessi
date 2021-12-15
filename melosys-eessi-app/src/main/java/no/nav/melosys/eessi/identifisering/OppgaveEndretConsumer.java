package no.nav.melosys.eessi.identifisering;

import java.util.Collection;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.integration.oppgave.HentOppgaveDto;
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository;
import no.nav.melosys.eessi.service.oppgave.OppgaveService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class OppgaveEndretConsumer {

    private final BucIdentifisertService bucIdentifisertService;
    private final BucIdentifiseringOppgRepository bucIdentifiseringOppgRepository;
    private final OppgaveService oppgaveService;
    private final IdentifiseringKontrollService identifiseringKontrollService;
    private final PersonFasade personFasade;

    private static final Collection<String> GYLDIGE_TEMA = Set.of("MED", "UFM");

    @KafkaListener(
        clientIdPrefix = "melosys-eessi-oppgaveEndret",
        topics = "${melosys.kafka.consumer.oppgave-endret.topic}",
        containerFactory = "oppgaveListenerContainerFactory",
        groupId = "${melosys.kafka.consumer.oppgave-endret.groupid}")
    public void oppgaveEndret(ConsumerRecord<String, OppgaveEndretHendelse> consumerRecord) {
        final var oppgave = consumerRecord.value();
        log.debug("Oppgave endret: {}", oppgave);

        if (erValidertIdentifiseringsoppgave(oppgave)) {
            log.info("Oppgave {} markert som identifisert av ID og Fordeling. Søker etter tilknyttet RINA-sak", oppgave.getId());
            bucIdentifiseringOppgRepository.findByOppgaveId(oppgave.getId().toString())
                .ifPresentOrElse(
                    b -> kontrollerIdentifiseringOgOppdaterOppgave(b.getRinaSaksnummer(), oppgave),
                    () -> log.debug("Finner ikke RINA-sak tilknytning for oppgave {}", oppgave.getId())
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
            log.info("Kan ikke behandle oppgave endret {}, versjonskonflikt mellom kafkamelding (versjon {}) og oppgave (versjon {}) ", oppgaveId, oppgaveEndretHendelse.getVersjon(),oppgaveDto.getVersjon());
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
                                                           OppgaveEndretHendelse oppgave) {
        var kontrollResultat = identifiseringKontrollService.kontrollerIdentifisertPerson(oppgave.hentAktørID(), rinaSaksnummer, oppgave.getVersjon());
        if (kontrollResultat.erIdentifisert()) {
            log.info("BUC {} identifisert av oppgave {}", rinaSaksnummer, oppgave.getId());
            bucIdentifisertService.lagreIdentifisertPerson(rinaSaksnummer, personFasade.hentNorskIdent(oppgave.hentAktørID()));
            oppgaveService.ferdigstillOppgave(oppgave.getId().toString(), oppgave.getVersjon());
        } else {
            log.info("Oppgave {} tilhørende rina-sak {} ikke identifisert. Feilet på: {}", oppgave.getId(), rinaSaksnummer, kontrollResultat.getBegrunnelser());
            oppgaveService.flyttOppgaveTilIdOgFordeling(
                oppgave.getId().toString(),
                oppgave.getVersjon(),
                kontrollResultat.hentFeilIOpplysningerTekst());
        }
    }
}
