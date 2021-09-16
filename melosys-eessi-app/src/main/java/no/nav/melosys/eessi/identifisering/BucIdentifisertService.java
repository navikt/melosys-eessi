package no.nav.melosys.eessi.identifisering;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.identifisering.event.BucIdentifisertEvent;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.models.BucIdentifisert;
import no.nav.melosys.eessi.repository.BucIdentifisertRepository;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BucIdentifisertService {

    private final BucIdentifisertRepository bucIdentifisertRepository;
    private final SaksrelasjonService saksrelasjonService;
    private final PersonFasade personFasade;
    private final ApplicationEventPublisher applicationEventPublisher;

    public BucIdentifisertService(BucIdentifisertRepository bucIdentifisertRepository,
                                  SaksrelasjonService saksrelasjonService,
                                  PersonFasade personFasade,
                                  ApplicationEventPublisher applicationEventPublisher) {
        this.bucIdentifisertRepository = bucIdentifisertRepository;
        this.saksrelasjonService = saksrelasjonService;
        this.personFasade = personFasade;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Henter ident fra saksrelasjon, eller fra buc_identifisert tabell om førstnevnte ikke finnes (vil oppstå for saker eldre enn tabellen)
     */
    public Optional<String> finnIdentifisertPerson(String rinaSaksnummer) {
        var ident = saksrelasjonService.finnAktørIDTilhørendeRinasak(rinaSaksnummer)
            .map(personFasade::hentNorskIdent);

        if (ident.isPresent()) {
            return ident;
        }

        return bucIdentifisertRepository.findByRinaSaksnummer(rinaSaksnummer)
            .map(BucIdentifisert::getFolkeregisterident);
    }

    public void lagreIdentifisertPerson(String rinaSaksnummer, String ident) {
        bucIdentifisertRepository.findByRinaSaksnummer(rinaSaksnummer)
            .ifPresentOrElse(
                i -> log.info("Rinasak {} allerede identifisert", rinaSaksnummer),
                () -> {
                    bucIdentifisertRepository.save(new BucIdentifisert(null, rinaSaksnummer, ident));
                    applicationEventPublisher.publishEvent(new BucIdentifisertEvent(rinaSaksnummer, ident));
                }
            );
    }
}
