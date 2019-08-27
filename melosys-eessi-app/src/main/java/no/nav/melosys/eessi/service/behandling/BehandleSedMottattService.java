package no.nav.melosys.eessi.service.behandling;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiProducer;
import no.nav.melosys.eessi.kafka.producers.mapping.MelosysEessiMeldingMapper;
import no.nav.melosys.eessi.kafka.producers.mapping.MelosysEessiMeldingMapperFactory;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.identifisering.PersonIdentifiseringService;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import no.nav.melosys.eessi.service.joark.SakInformasjon;
import no.nav.melosys.eessi.service.tps.TpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BehandleSedMottattService {

    private final OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;
    private final EuxService euxService;
    private final TpsService tpsService;
    private final MelosysEessiProducer melosysEessiProducer;
    private final PersonIdentifiseringService personIdentifiseringService;

    @Autowired
    public BehandleSedMottattService(
            OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService,
            EuxService euxService,
            TpsService tpsService,
            MelosysEessiProducer melosysEessiProducer,
            PersonIdentifiseringService personIdentifiseringService) {
        this.opprettInngaaendeJournalpostService = opprettInngaaendeJournalpostService;
        this.euxService = euxService;
        this.tpsService = tpsService;
        this.melosysEessiProducer = melosysEessiProducer;
        this.personIdentifiseringService = personIdentifiseringService;
    }

    public void behandleSed(SedHendelse sedMottatt) {

        try {
            SED sed = euxService.hentSed(sedMottatt.getRinaSakId(), sedMottatt.getRinaDokumentId());

            Optional<String> ident = personIdentifiseringService.identifiserPerson(sedMottatt, sed);
            if (ident.isPresent()) { //TODO: person identifisert, SED støtter ikke automatisk behandling MELOSYS-2903
                log.info("Person i rinaSak {} er identifisert", sedMottatt.getRinaSakId());
                sedMottatt.setNavBruker(ident.get());
                personErIdentifisert(sedMottatt, sed);
            } else {
                log.info("Person i rinasak {} ikke identifisert", sedMottatt.getRinaSakId());
                personIkkeIdentifisert(sedMottatt, sed);
            }

        } catch (IntegrationException | NotFoundException e) {
            log.error("Behandling av sed {} ble ikke fullført. Melding: {}", sedMottatt.getRinaDokumentId(), e.getMessage(), e);
        }
    }

    private void personIkkeIdentifisert(SedHendelse sedMottatt, SED sed) throws IntegrationException {
        String journalpostID = opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(
                sedMottatt, euxService.hentSedPdf(sedMottatt.getRinaSakId(), sedMottatt.getRinaDokumentId())
        );

        //TODO: opprett Oppgave til ID og fordeling

    }

    private void personErIdentifisert(SedHendelse sedMottatt, SED sed) throws IntegrationException, NotFoundException {
        String aktoerId = tpsService.hentAktoerId(sedMottatt.getNavBruker());
        SakInformasjon sakInformasjon = opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(
                sedMottatt, aktoerId, euxService.hentSedPdf(sedMottatt.getRinaSakId(), sedMottatt.getRinaDokumentId()));

        publiserMelosysEessiMelding(aktoerId, sed, sedMottatt, sakInformasjon);

        log.info("Behandling av innkommende sed {} fullført.", sedMottatt.getRinaDokumentId());
    }


    private void publiserMelosysEessiMelding(String aktoerId, SED sed, SedHendelse sedHendelse, SakInformasjon sakInformasjon) throws IntegrationException {

        SedType sedType = SedType.valueOf(sed.getSed());
        MelosysEessiMeldingMapper mapper = MelosysEessiMeldingMapperFactory.getMapper(sedType);
        if (mapper != null) {
            boolean sedErEndring = euxService.sedErEndring(sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId());
            melosysEessiProducer.publiserMelding(
                    mapper.map(aktoerId, sed, sedHendelse, sakInformasjon, sedErEndring)
            );
        }
    }
}
