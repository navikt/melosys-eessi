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
        containerFactory = "oppgaveListenerContainerFactory")
    public void oppgaveEndret(ConsumerRecord<String, OppgaveEndretHendelse> consumerRecord) {
        final var oppgave = consumerRecord.value();
        log.debug("Oppgave endret: {}", oppgave);

        if (erIdentifisertOppgave(oppgave)) {
            log.info("Oppgave {} markert som identifisert av ID og Fordeling. Søker etter tilknyttet RINA-sak", oppgave.getId());
            bucIdentifiseringOppgRepository.findByOppgaveId(oppgave.getId().toString())
                .ifPresentOrElse(
                    b -> kontrollerOgOppdaterOppgave(b.getRinaSaksnummer(), oppgave),
                    () -> log.debug("Finner ikke RINA-sak tilknytning for oppgave {}", oppgave.getId())
                );
        }
    }

    private boolean erIdentifisertOppgave(OppgaveEndretHendelse oppgaveEndretHendelse) {
        HentOppgaveDto oppgaveDto = oppgaveService.hentOppgave(oppgaveEndretHendelse.getId().toString());
        return "JFR".equals(oppgaveEndretHendelse.getOppgavetype())
            && "4530".equals(oppgaveEndretHendelse.getTildeltEnhetsnr())
            && oppgaveEndretHendelse.harAktørID()
            && GYLDIGE_TEMA.contains(oppgaveEndretHendelse.getTema())
            && oppgaveEndretHendelse.erÅpen()
            && oppgaveEndretHendelse.harMetadataRinasaksnummer()
            && oppgaveEndretHendelse.erRiktigVersjon(oppgaveDto.getVersjon());
    }

    private void kontrollerOgOppdaterOppgave(String rinaSaksnummer,
                                           OppgaveEndretHendelse oppgave) {
        var kontrollResultat = identifiseringKontrollService.kontrollerIdentifisertPerson(oppgave.hentAktørID(), rinaSaksnummer);
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
