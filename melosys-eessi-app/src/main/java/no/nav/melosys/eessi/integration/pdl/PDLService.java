package no.nav.melosys.eessi.integration.pdl;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.integration.pdl.dto.PDLStatsborgerskap;
import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;
import no.nav.melosys.eessi.service.tps.personsok.PersonSoekResponse;
import no.nav.melosys.eessi.service.tps.personsok.PersonsoekKriterier;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

import static no.nav.melosys.eessi.integration.pdl.PDLUtils.hentSisteOpplysning;

@Component
public class PDLService implements PersonFasade {

    private static final String IKKE_IMPLEMENTERT = "Ikke implementert";

    private final PDLConsumer pdlConsumer;

    public PDLService(PDLConsumer pdlConsumer) {
        this.pdlConsumer = pdlConsumer;
    }

    @Override
    public PersonModell hentPerson(String ident) {
        var pdlPerson = pdlConsumer.hentPerson(ident);

        var personModellBuilder = PersonModell.builder().ident(ident);

        hentSisteOpplysning(pdlPerson.getNavn()).ifPresent(navn -> {
            personModellBuilder.fornavn(navn.getFornavn());
            personModellBuilder.etternavn(navn.getEtternavn());
        });

        hentSisteOpplysning(pdlPerson.getFoedsel())
                .ifPresent(fødsel -> personModellBuilder.fødselsdato(fødsel.getFoedselsdato()));
        hentSisteOpplysning(pdlPerson.getFolkeregisterpersonstatus())
                .ifPresent(status -> personModellBuilder.erOpphørt(status.statusErOpphørt()));
        personModellBuilder.statsborgerskapLandkodeISO2(
                pdlPerson.getStatsborgerskap().stream()
                .map(PDLStatsborgerskap::getLand)
                .map(LandkodeMapper::getLandkodeIso2)
                .collect(Collectors.toSet())
        );

        return personModellBuilder.build();
    }

    @Override
    public String hentAktoerId(String ident) {
        throw new NotImplementedException(IKKE_IMPLEMENTERT);
    }

    @Override
    public String hentNorskIdent(String aktoerID) {
        throw new NotImplementedException(IKKE_IMPLEMENTERT);
    }

    @Override
    public List<PersonSoekResponse> soekEtterPerson(PersonsoekKriterier personsoekKriterier) {
        throw new NotImplementedException(IKKE_IMPLEMENTERT);
    }
}
