package no.nav.melosys.eessi.service.behandling;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.stream.Stream;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiMeldingMapper;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiProducer;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.exception.ValidationException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Statsborgerskap;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import no.nav.melosys.eessi.service.joark.SakInformasjon;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;
import no.nav.melosys.eessi.service.tps.TpsService;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BehandleSedMottattService {

    private final OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;
    private final EuxService euxService;
    private final TpsService tpsService;
    private final MelosysEessiProducer melosysEessiProducer;

    @Autowired
    public BehandleSedMottattService(OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService,
            EuxService euxService,
            TpsService tpsService, MelosysEessiProducer melosysEessiProducer) {
        this.opprettInngaaendeJournalpostService = opprettInngaaendeJournalpostService;
        this.euxService = euxService;
        this.tpsService = tpsService;
        this.melosysEessiProducer = melosysEessiProducer;
    }

    public void behandleSed(SedHendelse sedMottatt) {

        try {
            SED sed = euxService.hentSed(sedMottatt.getRinaSakId(), sedMottatt.getRinaDokumentId());
            vurderPerson(sedMottatt, sed);
            log.info("Person i rinaSak {} er verifisert mot TPS", sedMottatt.getRinaSakId());

            String aktoerId = tpsService.hentAktoerId(sedMottatt.getNavBruker());
            SakInformasjon sakInformasjon = opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(sedMottatt, aktoerId);
            log.info("Midlertidig journalpost opprettet med id {}", sakInformasjon.getJournalpostId());

            if (MelosysEessiMeldingMapper.isSupportedSed(sed)) {
                melosysEessiProducer.publiserMelding(MelosysEessiMeldingMapper.map(aktoerId,sed, sedMottatt, sakInformasjon));
            }

        } catch (IntegrationException | NotFoundException | ValidationException e) {
            log.error("Behandling av sed {} ble ikke fullført. Melding: {}", sedMottatt.getRinaDokumentId(), e.getMessage(), e);
        }

        log.info("Behandling av innkommende sed {} fullført.", sedMottatt.getRinaDokumentId());
    }

    /**
     * Vurderer om personen er kjent fra før (i TPS) ut fra følgende opplysninger:
     * - Fødselsdato
     * - Statsborgerskap
     *
     * @param sedMottatt Objekt som blir mottatt fra kafka-køen
     * @param sed        SED-dokument hentet fra eux
     * @throws NotFoundException   Dersom det ikke er oppgitt en norsk ident i sedMottatt eller dersom
     *                             person ikke blir funnet i TPS
     * @throws ValidationException Dersom opplysninger om person hentet fra TPS ikke stemmer overens med opplysninger
     *                             i SED eller dersom tpsService ikke klarer å hente person fra TPS
     */
    private void vurderPerson(SedHendelse sedMottatt, SED sed) throws NotFoundException, ValidationException {

        if (StringUtils.isEmpty(sedMottatt.getNavBruker())) {
            // TODO: Venter på avklaringer som beskriver hva som skal gjøres når ingen norsk ident er oppgitt i SED.
            throw new NotFoundException("Ingen norsk ident funnet i sed");
        }

        try {
            Person person = tpsService.hentPerson(sedMottatt.getNavBruker());

            if (!harSammeStatsborgerskap(person, sed) || !harSammeFoedselsdato(person, sed)) {
                // TODO: Person i SED hadde ikke samme opplysninger i TPS. Venter på avklaringer.
                throw new ValidationException("Kunne ikke vurdere person");
            }
        } catch (HentPersonPersonIkkeFunnet hentPersonPersonIkkeFunnet) {
            // TODO: Person ble ikke funnet i TPS. Venter på avklaringer.
            throw new NotFoundException("Person ble ikke funnet i TPS: " + hentPersonPersonIkkeFunnet.getMessage());
        } catch (HentPersonSikkerhetsbegrensning hentPersonSikkerhetsbegrensning) {
            throw new ValidationException("Kunne ikke hente person fra TPS: " + hentPersonSikkerhetsbegrensning.getMessage());
        }
    }

    /**
     * Sjekker om person mottatt fra TPS har samme statsborgerskap som person i SED.
     *
     * @param person Person mottatt fra TPS
     * @param sed    SED mottatt fra kafka-kø
     * @return true dersom person og sed har samme statsborgerskap
     */
    private boolean harSammeStatsborgerskap(Person person, SED sed) throws NotFoundException {
        String tpsStatsborgerskap = LandkodeMapper.getLandkodeIso2(person.getStatsborgerskap().getLand().getValue());
        Stream<String> sedStatsborgerskap = sed.getNav().getBruker().getPerson().getStatsborgerskap()
                .stream().map(Statsborgerskap::getLand);

        return sedStatsborgerskap.anyMatch(tpsStatsborgerskap::equalsIgnoreCase);
    }

    /**
     * Sjekker om person mottatt fra TPS har samme fødselsdato som person i SED.
     *
     * @param person Person mottatt fra TPS
     * @param sed    SED mottatt fra kafka-kø
     * @return true dersom person og sed har samme fødselsdato
     */
    private boolean harSammeFoedselsdato(Person person, SED sed) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        Calendar tpsFoedselsdatoCalendar = person.getFoedselsdato().getFoedselsdato().toGregorianCalendar();
        dateFormatter.setTimeZone(tpsFoedselsdatoCalendar.getTimeZone());

        String tpsFoedselsdato = dateFormatter.format(tpsFoedselsdatoCalendar.getTime());
        String sedFoedselsdato = sed.getNav().getBruker().getPerson().getFoedselsdato();

        return tpsFoedselsdato.equalsIgnoreCase(sedFoedselsdato);
    }
}
