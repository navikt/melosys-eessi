package no.nav.melosys.eessi.service.identifisering;

import java.util.Comparator;

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
        sedMottattHendelseRepository.findAllByPublisertKafka(false).stream()
                .filter(hendelse -> rinaSaksnummer.equals(hendelse.getSedHendelse().getRinaSakId()))
                .sorted(Comparator.comparing(SedMottattHendelse::getMottattDato))
                .forEach(it -> {
                    var mapper = MelosysEessiMeldingMapperFactory.getMapper(SedType.valueOf(it.getSedHendelse().getSedType()));
                    melosysEessiProducer.publiserMelding(
                            mapper.map(aktoerId, euxService.hentSed(it.getSedHendelse().getRinaSakId(), it.getSedHendelse().getRinaDokumentId()),
                                    it.getSedHendelse().getRinaDokumentId(), it.getSedHendelse().getRinaSakId(),
                                    it.getSedHendelse().getSedType(), it.getSedHendelse().getBucType(), it.getSedHendelse().getAvsenderId(), it.getSedHendelse().getLandkode(),
                                    it.getJournalpostId(), null,
                                    saksrelasjonService.finnVedRinaSaksnummer(it.getSedHendelse().getRinaSakId()).map(FagsakRinasakKobling::getGsakSaksnummer).map(Object::toString).orElse(null),
                                    euxService.sedErEndring(it.getSedHendelse().getRinaDokumentId(), it.getSedHendelse().getRinaSakId()),
                                    it.getSedHendelse().getRinaDokumentVersjon())
                    );
                    it.setPublisertKafka(true);
                });
    }
}
