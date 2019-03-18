package no.nav.melosys.eessi.service.tps;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.aktoer.AktoerConsumer;
import no.nav.melosys.eessi.integration.tps.PersonConsumer;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.AktoerId;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Person;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TpsService {

    private final PersonConsumer personConsumer;
    private final AktoerConsumer aktoerConsumer;

    @Autowired
    public TpsService(PersonConsumer personConsumer, AktoerConsumer aktoerConsumer) {
        this.personConsumer = personConsumer;
        this.aktoerConsumer = aktoerConsumer;
    }

    public Person hentPerson(String ident) throws HentPersonPersonIkkeFunnet, HentPersonSikkerhetsbegrensning {
        HentPersonRequest request = new HentPersonRequest()
                .withAktoer(new AktoerId()
                        .withAktoerId(ident));

        HentPersonResponse response = personConsumer.hentPerson(request);

        return response.getPerson();
    }

    public Person hentPersonMedAdresse(String ident) throws HentPersonPersonIkkeFunnet, HentPersonSikkerhetsbegrensning {
        HentPersonRequest request = new HentPersonRequest()
                .withInformasjonsbehov(Informasjonsbehov.ADRESSE)
                .withAktoer(new AktoerId()
                        .withAktoerId(ident));

        HentPersonResponse response = personConsumer.hentPerson(request);

        return response.getPerson();
    }

    public String hentAktoerId(String ident) {
        return aktoerConsumer.getAktoerId(ident);
    }
}
