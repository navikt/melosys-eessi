package no.nav.melosys.eessi.identifisering;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.finn.unleash.Unleash;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiAivenProducer;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiProducer;
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class BehandleBucIdentifisertService {

    private final SedMottattHendelseRepository sedMottattHendelseRepository;
    private final SaksrelasjonService saksrelasjonService;
    private final EuxService euxService;
    private final MelosysEessiProducer melosysEessiProducer;
    private final MelosysEessiMeldingMapperFactory melosysEessiMeldingMapperFactory;
    private final MelosysEessiAivenProducer melosysEessiAivenProducer;
    private final Unleash unleash;

    @Transactional
    public void bucIdentifisert(String rinaSaksnummer, String aktoerId) {
        sedMottattHendelseRepository.findAllByRinaSaksnummerAndPublisertKafkaSortedByMottattDato(rinaSaksnummer, false)
            .stream()
            .filter(sedMottattHendelse -> sedMottattHendelse.getSedHendelse().erIkkeX100())
            .forEach(sedMottattHendelse -> publiserMelding(sedMottattHendelse, aktoerId));
    }

    private void publiserMelding(SedMottattHendelse sedMottattHendelse, String aktørID) {
        final var mapper = melosysEessiMeldingMapperFactory.getMapper(SedType.valueOf(sedMottattHendelse.getSedHendelse().getSedType()));
        final var sed = euxService.hentSed(sedMottattHendelse.getSedHendelse().getRinaSakId(), sedMottattHendelse.getSedHendelse().getRinaDokumentId());
        final var sedErEndring = euxService.sedErEndring(sedMottattHendelse.getSedHendelse().getRinaDokumentId(), sedMottattHendelse.getSedHendelse().getRinaSakId());
        final var arkivsakID = saksrelasjonService.finnVedRinaSaksnummer(sedMottattHendelse.getSedHendelse().getRinaSakId())
            .map(FagsakRinasakKobling::getGsakSaksnummer)
            .map(Object::toString)
            .orElse(null);

        log.info("Publiserer melding om SED mottatt. SED: {}", sedMottattHendelse.getSedHendelse().getSedId());
        MelosysEessiMelding melosysEessiMelding = mapper.map(
            aktørID,
            sed,
            sedMottattHendelse.getSedHendelse().getRinaDokumentId(),
            sedMottattHendelse.getSedHendelse().getRinaSakId(),
            sedMottattHendelse.getSedHendelse().getSedType(),
            sedMottattHendelse.getSedHendelse().getBucType(),
            sedMottattHendelse.getSedHendelse().getAvsenderId(),
            sedMottattHendelse.getSedHendelse().getLandkode(),
            sedMottattHendelse.getJournalpostId(), null,
            arkivsakID,
            sedErEndring,
            sedMottattHendelse.getSedHendelse().getRinaDokumentVersjon());

        if (unleash.isEnabled("melosys.eessi.aiven-producer")) {
            log.info("Publiserer eessiMelding melding på aiven");
            melosysEessiAivenProducer.publiserMelding(melosysEessiMelding);
        } else {
            melosysEessiProducer.publiserMelding(melosysEessiMelding);
        }

        sedMottattHendelse.setPublisertKafka(true);
    }
}
