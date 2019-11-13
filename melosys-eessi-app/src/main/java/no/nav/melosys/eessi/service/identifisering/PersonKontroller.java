package no.nav.melosys.eessi.service.identifisering;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Statsborgerskap;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Person;

@UtilityClass
class PersonKontroller {

    private static final String UTGAATT_PERSON = "UTPE";
    private static final String UTGAATT_PERSON_ANNULLERT_TILGANG = "UTAN";

    static boolean erOpphoert(Person person) {
        return Arrays.asList(UTGAATT_PERSON_ANNULLERT_TILGANG, UTGAATT_PERSON)
                .contains(person.getPersonstatus().getPersonstatus().getValue());
    }

    static boolean harSammeStatsborgerskap(Person person, SED sed) throws NotFoundException {
        String tpsStatsborgerskap = LandkodeMapper.getLandkodeIso2(person.getStatsborgerskap().getLand().getValue());
        Stream<String> sedStatsborgerskap = sed.getNav().getBruker().getPerson().getStatsborgerskap()
                .stream().map(Statsborgerskap::getLand);

        return sedStatsborgerskap.anyMatch(tpsStatsborgerskap::equalsIgnoreCase);
    }

    static boolean harSammeFoedselsdato(Person person, SED sed) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        Calendar tpsFoedselsdatoCalendar = person.getFoedselsdato().getFoedselsdato().toGregorianCalendar();
        dateFormatter.setTimeZone(tpsFoedselsdatoCalendar.getTimeZone());

        String tpsFoedselsdato = dateFormatter.format(tpsFoedselsdatoCalendar.getTime());
        String sedFoedselsdato = sed.getNav().getBruker().getPerson().getFoedselsdato();

        return tpsFoedselsdato.equalsIgnoreCase(sedFoedselsdato);
    }
}
