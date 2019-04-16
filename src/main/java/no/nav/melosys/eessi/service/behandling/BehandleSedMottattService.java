package no.nav.melosys.eessi.service.behandling;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiMeldingMapper;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiProducer;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import no.nav.melosys.eessi.service.joark.SakInformasjon;
import no.nav.melosys.eessi.service.tps.TpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class BehandleSedMottattService {

    private final OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;
    private final EuxService euxService;
    private final TpsService tpsService;
    private final MelosysEessiProducer melosysEessiProducer;
    private final Personvurdering personvurdering;

    @Autowired
    public BehandleSedMottattService(
            OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService,
            EuxService euxService,
            TpsService tpsService,
            MelosysEessiProducer melosysEessiProducer,
            Personvurdering personvurdering) {
        this.opprettInngaaendeJournalpostService = opprettInngaaendeJournalpostService;
        this.euxService = euxService;
        this.tpsService = tpsService;
        this.melosysEessiProducer = melosysEessiProducer;
        this.personvurdering = personvurdering;
    }

    public void behandleSed(SedHendelse sedMottatt) {

        try {
            SED sed = euxService.hentSed(sedMottatt.getRinaSakId(), sedMottatt.getRinaDokumentId());

            String ident = personvurdering.hentNorskIdent(sedMottatt, sed);
            if (StringUtils.isEmpty(ident)) {
                throw new NotFoundException("Ingen norsk ident ble funnet for rinaSak " + sedMottatt.getRinaSakId());
            }
            sedMottatt.setNavBruker(ident);
            log.info("Person i rinaSak {} er verifisert mot TPS", sedMottatt.getRinaSakId());

            String aktoerId = tpsService.hentAktoerId(sedMottatt.getNavBruker());
            SakInformasjon sakInformasjon = opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(sedMottatt, aktoerId);
            log.info("Midlertidig journalpost opprettet med id {}", sakInformasjon.getJournalpostId());

            if (MelosysEessiMeldingMapper.isSupportedSed(sed)) {
                melosysEessiProducer.publiserMelding(MelosysEessiMeldingMapper.map(aktoerId, sed, sedMottatt, sakInformasjon));
            }

            log.info("Behandling av innkommende sed {} fullført.", sedMottatt.getRinaDokumentId());
        } catch (IntegrationException | NotFoundException e) {
            log.error("Behandling av sed {} ble ikke fullført. Melding: {}", sedMottatt.getRinaDokumentId(), e.getMessage(), e);
        }
    }
}
