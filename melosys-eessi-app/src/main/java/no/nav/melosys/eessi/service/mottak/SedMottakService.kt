package no.nav.melosys.eessi.service.mottak;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.identifisering.BucIdentifisertService;
import no.nav.melosys.eessi.identifisering.FnrUtils;
import no.nav.melosys.eessi.identifisering.PersonIdentifisering;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.IdentRekvisisjonTilMellomlagringMapper;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.metrikker.SedMetrikker;
import no.nav.melosys.eessi.models.BucIdentifiseringOppg;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Participant;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Person;
import no.nav.melosys.eessi.models.sed.nav.Pin;
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository;
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.journalfoering.OpprettInngaaendeJournalpostService;
import no.nav.melosys.eessi.service.journalpostkobling.JournalpostSedKoblingService;
import no.nav.melosys.eessi.service.oppgave.OppgaveService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class SedMottakService {

    private final EuxService euxService;
    private final PersonFasade personFasade;
    private final OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;
    private final OppgaveService oppgaveService;
    private final SedMottattHendelseRepository sedMottattHendelseRepository;
    private final BucIdentifiseringOppgRepository bucIdentifiseringOppgRepository;
    private final JournalpostSedKoblingService journalpostSedKoblingService;
    private final SedMetrikker sedMetrikker;
    private final PersonIdentifisering personIdentifisering;
    private final BucIdentifisertService bucIdentifisertService;

    @Value("${rina.institusjon-id}")
    private String rinaInstitusjonsId;

    @Transactional
    public void behandleSedMottakHendelse(SedMottattHendelse sedMottattHendelse) {
        if (sedMottattHendelse.getSedHendelse().erX100()) {
            log.info("Ignorerer mottatt SED {} av typen X100", sedMottattHendelse.getSedHendelse().getSedId());
            return;
        }

        if (sedMottattHendelseRepository.findBySedID(sedMottattHendelse.getSedHendelse().getSedId()).isPresent()) {
            log.info("Mottatt SED {} er allerede behandlet", sedMottattHendelse.getSedHendelse().getSedId());
            return;
        }

        if (erXSedBehandletUtenASed(sedMottattHendelse.getSedHendelse())) {
            throw new IllegalStateException("Mottatt SED %s av type %s har ikke tilhørende A sed behandlet"
                .formatted(sedMottattHendelse.getSedHendelse().getSedId(), sedMottattHendelse.getSedHendelse().getSedType()));
        }

        var lagretHendelse = sedMottattHendelseRepository.save(sedMottattHendelse);

        final var sed = euxService.hentSedMedRetry(sedMottattHendelse.getSedHendelse().getRinaSakId(),
            sedMottattHendelse.getSedHendelse().getRinaDokumentId());

        log.info("Søker etter person for SED");
        personIdentifisering.identifiserPerson(lagretHendelse.getSedHendelse().getRinaSakId(), sed)
            .ifPresentOrElse(
                ident -> bucIdentifisertService.lagreIdentifisertPerson(lagretHendelse.getSedHendelse().getRinaSakId(), ident),
                () -> opprettOppgaveIdentifisering(lagretHendelse, sed)
            );

        sedMetrikker.sedMottatt(sedMottattHendelse.getSedHendelse().getSedType());
    }

    private boolean erXSedBehandletUtenASed(SedHendelse sedHendelse) {
        if (!sedHendelse.erXSedSomTrengerKontroll()) return false;

        if (sedHendelse.getSedType().equals(SedType.X007.name())) {
            BUC buc = euxService.hentBuc(sedHendelse.getRinaSakId());

            boolean sedTypeErX007OgNorgeErSakseier = buc.getParticipants().stream()
                .anyMatch(p -> p.getRole().equals(Participant.ParticipantRole.SAKSEIER)
                    && p.getOrganisation().getId().equals(rinaInstitusjonsId));

            if (sedTypeErX007OgNorgeErSakseier) return false;
        }

        return !journalpostSedKoblingService.erASedAlleredeBehandlet(sedHendelse.getRinaSakId());
    }

    private void opprettOppgaveIdentifisering(SedMottattHendelse sedMottatt, SED sed) {
        if (!sedMottatt.getSedHendelse().erASED()) {
            log.info("SED er ikke A-sed, oppretter ikke oppgave til ID og fordeling, SED: {}", sedMottatt.getSedHendelse().getSedId());
            return;
        }

        log.info("Oppretter oppgave til ID og fordeling for SED {}", sedMottatt.getSedHendelse().getSedId());

        final var rinaSaksnummer = sedMottatt.getSedHendelse().getRinaSakId();
        bucIdentifiseringOppgRepository.findByRinaSaksnummer(rinaSaksnummer)
            .stream()
            .filter(this::oppgaveErÅpen)
            .findFirst()
            .ifPresentOrElse(
                bucIdentifiseringOppg -> log.info("Identifiseringsoppgave {} finnes allerede for rinasak {}", bucIdentifiseringOppg.getOppgaveId(), rinaSaksnummer),
                () -> opprettOgLagreIdentifiseringsoppgave(sedMottatt, sed)
            );
    }

    private boolean oppgaveErÅpen(BucIdentifiseringOppg bucIdentifiseringOppg) {
        return oppgaveService.hentOppgave(bucIdentifiseringOppg.getOppgaveId()).erÅpen();
    }

    private void opprettOgLagreIdentifiseringsoppgave(SedMottattHendelse sedMottattHendelse, SED sed) {
        String journalpostID = opprettJournalpost(sedMottattHendelse, null);
        var oppgaveID = opprettOgLagreIndentifiseringsoppgave(sedMottattHendelse, sed, journalpostID);

        bucIdentifiseringOppgRepository.save(BucIdentifiseringOppg.builder()
            .rinaSaksnummer(sedMottattHendelse.getSedHendelse().getRinaSakId())
            .oppgaveId(oppgaveID)
            .versjon(1)
            .build());

        log.info("Opprettet oppgave med id {}", oppgaveID);
    }

    private String opprettOgLagreIndentifiseringsoppgave(SedMottattHendelse sedMottattHendelse, SED sed, String journalpostID) {
        var personFraSed = sed.finnPerson().orElse(null);

        if (personFraSed != null && !harNorskPersonnummer(personFraSed)) {
            var identRekvisjonTilMellomlagring = IdentRekvisisjonTilMellomlagringMapper.byggIdentRekvisisjonTilMellomlagring(sedMottattHendelse, sed);

            String lenkeForRekvirering = personFasade.opprettLenkeForRekvirering(identRekvisjonTilMellomlagring);

            return oppgaveService.opprettOppgaveTilIdOgFordeling(
                journalpostID,
                sedMottattHendelse.getSedHendelse().getSedType(),
                sedMottattHendelse.getSedHendelse().getRinaSakId(),
                lenkeForRekvirering
            );
        }
        return oppgaveService.opprettOppgaveTilIdOgFordeling(
            journalpostID,
            sedMottattHendelse.getSedHendelse().getSedType(),
            sedMottattHendelse.getSedHendelse().getRinaSakId()
        );

    }

    private boolean harNorskPersonnummer(Person personFraSed) {
        return personFraSed.finnNorskPin()
            .map(Pin::getIdentifikator)
            .flatMap(FnrUtils::filtrerUtGyldigNorskIdent).isPresent();
    }

    private String opprettJournalpost(SedMottattHendelse sedMottattHendelse, String navIdent) {
        log.info("Oppretter journalpost for SED {}", sedMottattHendelse.getSedHendelse().getRinaDokumentId());
        var sedMedVedlegg = euxService.hentSedMedVedlegg(
            sedMottattHendelse.getSedHendelse().getRinaSakId(), sedMottattHendelse.getSedHendelse().getRinaDokumentId()
        );

        String journalpostID = opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(
            sedMottattHendelse.getSedHendelse(), sedMedVedlegg, navIdent);

        sedMottattHendelse.setJournalpostId(journalpostID);
        sedMottattHendelseRepository.save(sedMottattHendelse);
        return journalpostID;
    }
}
