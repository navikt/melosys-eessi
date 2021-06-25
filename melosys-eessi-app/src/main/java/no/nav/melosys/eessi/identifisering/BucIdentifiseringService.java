package no.nav.melosys.eessi.identifisering;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiProducer;
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
public class BucIdentifiseringService {

    private final SedMottattHendelseRepository sedMottattHendelseRepository;
    private final SaksrelasjonService saksrelasjonService;
    private final EuxService euxService;
    private final MelosysEessiProducer melosysEessiProducer;

    @Transactional
    public void bucIdentifisert(String rinaSaksnummer, String aktoerId) {
        sedMottattHendelseRepository.findAllByRinaSaksnummeerAndPublisertKafkaSortedByMottattDato(rinaSaksnummer, false)
                .forEach(sedMottattHendelse -> publiserMelding(sedMottattHendelse, aktoerId));
    }

    private void publiserMelding(SedMottattHendelse sedMottattHendelse, String aktørID) {
        final var mapper = MelosysEessiMeldingMapperFactory.getMapper(SedType.valueOf(sedMottattHendelse.getSedHendelse().getSedType()));
        final var sed = euxService.hentSed(sedMottattHendelse.getSedHendelse().getRinaSakId(), sedMottattHendelse.getSedHendelse().getRinaDokumentId());
        final var sedErEndring = euxService.sedErEndring(sedMottattHendelse.getSedHendelse().getRinaDokumentId(), sedMottattHendelse.getSedHendelse().getRinaSakId());
        final var arkivsakID = saksrelasjonService.finnVedRinaSaksnummer(sedMottattHendelse.getSedHendelse().getRinaSakId())
                .map(FagsakRinasakKobling::getGsakSaksnummer)
                .map(Object::toString)
                .orElse(null);

        melosysEessiProducer.publiserMelding(
                mapper.map(
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
                        sedMottattHendelse.getSedHendelse().getRinaDokumentVersjon())
        );

        sedMottattHendelse.setPublisertKafka(true);
    }
}
