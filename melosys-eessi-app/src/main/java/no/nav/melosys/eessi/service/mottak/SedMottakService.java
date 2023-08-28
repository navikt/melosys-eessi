package no.nav.melosys.eessi.service.mottak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.finn.unleash.Unleash;
import no.nav.melosys.eessi.identifisering.BucIdentifisertService;
import no.nav.melosys.eessi.identifisering.FnrUtils;
import no.nav.melosys.eessi.identifisering.PersonIdentifisering;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.integration.pdl.dto.PDLKjoennType;
import no.nav.melosys.eessi.integration.pdl.dto.sed.DnummerRekvisjonTilMellomlagring;
import no.nav.melosys.eessi.integration.pdl.dto.sed.DnummerRekvisisjonKilde;
import no.nav.melosys.eessi.integration.pdl.dto.sed.DnummerRekvisisjonPersonopplysninger;
import no.nav.melosys.eessi.integration.pdl.dto.sed.DnummerRekvisisjonUtenlandskIdentifikasjon;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.metrikker.SedMetrikker;
import no.nav.melosys.eessi.models.BucIdentifiseringOppg;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Participant;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Kjønn;
import no.nav.melosys.eessi.models.sed.nav.Person;
import no.nav.melosys.eessi.models.sed.nav.Pin;
import no.nav.melosys.eessi.models.sed.nav.Statsborgerskap;
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository;
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import no.nav.melosys.eessi.service.journalpostkobling.JournalpostSedKoblingService;
import no.nav.melosys.eessi.service.oppgave.OppgaveService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.stream.Collectors;


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

    private final Unleash unleash;


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
        log.info("Oppretter oppgave til ID og fordeling for SED {}", sedMottatt.getSedHendelse().getRinaDokumentId());

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
        boolean preutfyllingEnabled = unleash.isEnabled("melosys.eessi.preutfylling.av.sed");

        var oppgaveID = "";
        var harNorskPersonnummer = true;
        var personFraSed = sed.finnPerson().orElse(null);

        log.info("[EESSI TEST] person fra sed: {}", personFraSed);
        if (personFraSed != null) {
            harNorskPersonnummer = personFraSed.finnNorskPin()
                    .map(Pin::getIdentifikator)
                    .flatMap(FnrUtils::filtrerUtGyldigNorskIdent).isPresent();
        }

        if (sedMottattHendelse.getSedHendelse().erASED() && !harNorskPersonnummer && preutfyllingEnabled) {
            var pinSEDErFraLandSedKommerFra = personFraSed.getPin().stream().anyMatch(a -> a.getLand().equals(sedMottattHendelse.getSedHendelse().getLandkode()));

            var pdlSed = byggPDLSed(sedMottattHendelse, sed, personFraSed, pinSEDErFraLandSedKommerFra);
            log.info("[EESSI TEST] Prøver å rekvirere", pdlSed);

            try {
                String preutfylltLenkeForRekvirering = personFasade.hentPreutfylltLenkeForRekvirering(pdlSed);
                log.info("[EESSI TEST] Rekvirering OK: " + preutfylltLenkeForRekvirering);
            } catch (Exception e) {
                log.error("[EESSI TEST] Feil under rekvirering: " + e.getMessage());
                throw e;
            }


            oppgaveID = oppgaveService.opprettOppgaveTilIdOgFordeling(
                    journalpostID,
                    sedMottattHendelse.getSedHendelse().getSedType(),
                    sedMottattHendelse.getSedHendelse().getRinaSakId()
                    //Todo: Fyll ut her.
            );
        } else {
            oppgaveID = oppgaveService.opprettOppgaveTilIdOgFordeling(
                    journalpostID,
                    sedMottattHendelse.getSedHendelse().getSedType(),
                    sedMottattHendelse.getSedHendelse().getRinaSakId()
            );
        }

        bucIdentifiseringOppgRepository.save(BucIdentifiseringOppg.builder()
                .rinaSaksnummer(sedMottattHendelse.getSedHendelse().getRinaSakId())
                .oppgaveId(oppgaveID)
                .versjon(1)
                .build());

        log.info("Opprettet oppgave med id {}", oppgaveID);
    }

    private DnummerRekvisjonTilMellomlagring byggPDLSed(SedMottattHendelse sedMottattHendelse, SED sed, Person personFraSed, boolean pinSEDErFraLandSedKommerFra) {
        var pdlSed = new DnummerRekvisjonTilMellomlagring.Builder()
                .medKilde(new DnummerRekvisisjonKilde.Builder()
                        .medInstitusjon(hentInstitusjon(sed))
                        .medLandkode(sedMottattHendelse.getSedHendelse().getAvsenderId() != null ? sedMottattHendelse.getSedHendelse().getLandkode() : "")
                        .build())
                .medPersonopplysninger(new DnummerRekvisisjonPersonopplysninger.Builder()
                        .medEtternavn(personFraSed.getEtternavn())
                        .medFornavn(personFraSed.getFornavn())
                        .medFoedselsdato(personFraSed.getFoedselsdato())
                        .medKjoenn(hentPDLKjønn(personFraSed).name())
                        .medStatsborgerskap(personFraSed.getStatsborgerskap().stream().map(Statsborgerskap::getLand).collect(Collectors.toList()))
                        .medFoedeland(personFraSed.getFoedested() != null ? personFraSed.getFoedested().getLand() : "")
                        .build());

        if (pinSEDErFraLandSedKommerFra) {
            pdlSed.medUtenlandskIdentifikasjon(new DnummerRekvisisjonUtenlandskIdentifikasjon.Builder()
                    .medUtstederland(sedMottattHendelse.getSedHendelse().getLandkode()).build());
        } else {
            pdlSed.medUtenlandskIdentifikasjon(new DnummerRekvisisjonUtenlandskIdentifikasjon.Builder()
                    .medUtenlandskId("").build());
        }

        return pdlSed.build();
    }

    private PDLKjoennType hentPDLKjønn(Person personFraSed) {
        if (personFraSed.getKjoenn() == Kjønn.K) {
            return PDLKjoennType.KVINNE;
        } else if (personFraSed.getKjoenn() == Kjønn.M) {
            return PDLKjoennType.MANN;
        }
        return PDLKjoennType.UKJENT;

    }

    private String hentInstitusjon(SED sed) {
        if (sed.getNav() != null
                && sed.getNav().getSak() != null
                && sed.getNav().getSak().getFjerninstitusjon() != null
                && sed.getNav().getSak().getFjerninstitusjon().getInstitusjon() != null) {
            return sed.getNav().getSak().getFjerninstitusjon().getInstitusjon().getNavn();
        }

        return "";
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
