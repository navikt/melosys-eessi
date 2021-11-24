package no.nav.melosys.eessi.identifisering;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Creator;
import no.nav.melosys.eessi.models.buc.Document;
import no.nav.melosys.eessi.models.buc.Organisation;
import no.nav.melosys.eessi.models.person.Kjønn;
import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.models.person.UtenlandskId;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.*;
import no.nav.melosys.eessi.service.eux.EuxService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static no.nav.melosys.eessi.models.sed.nav.Kjønn.K;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdentifiseringKontrollServiceTest {

    @Mock
    private EuxService euxService;
    @Mock
    private PersonFasade personFasade;
    @Mock
    private PersonSokMetrikker personSokMetrikker;

    private IdentifiseringKontrollService identifiseringKontrollService;

    private final BUC buc = lagBuc();
    private final SED sed = new SED();

    private final LocalDate fødselsdato = LocalDate.now();
    private final Person sedPerson = new Person();
    private final String utenlandskId = "41545325643";
    private final String avsenderLand = "SE";
    private final Statsborgerskap statsborgerskap = new Statsborgerskap();
    private final String aktørID = "53254341";

    private final String rinaSaksnummer = "432534";
    private final String dokumentId = "abcdefghijkl1";

    private final PersonModell.PersonModellBuilder personBuilder = PersonModell.builder();


    @BeforeEach
    void setup() {
        identifiseringKontrollService = new IdentifiseringKontrollService(personFasade, euxService, personSokMetrikker);

        var utenlandskPin = new Pin(utenlandskId, avsenderLand, null);

        statsborgerskap.setLand(avsenderLand);

        sedPerson.setFoedselsdato(fødselsdato.toString());
        sedPerson.setKjoenn(K);
        sedPerson.setPin(Set.of(utenlandskPin));
        sedPerson.setStatsborgerskap(Set.of(statsborgerskap));

        sed.setNav(new Nav());
        sed.getNav().setBruker(new Bruker());
        sed.getNav().getBruker().setPerson(sedPerson);
        sed.setSedType(SedType.A009.name());

        when(euxService.finnBUC(rinaSaksnummer)).thenReturn(Optional.of(buc));
        when(euxService.hentSed(rinaSaksnummer, dokumentId)).thenReturn(sed);

        personBuilder
            .kjønn(Kjønn.KVINNE)
            .fødselsdato(fødselsdato)
            .statsborgerskapLandkodeISO2(Set.of(avsenderLand))
            .utenlandskId(Set.of(new UtenlandskId(utenlandskId, avsenderLand)));

    }

    private BUC lagBuc() {
        var buc = new BUC();
        buc.setCreator(new Creator());
        buc.getCreator().setOrganisation(new Organisation());
        buc.getCreator().getOrganisation().setCountryCode(avsenderLand);

        var dokument = new Document();
        dokument.setId(dokumentId);
        dokument.setDirection("IN");
        dokument.setStatus("CREATED");
        dokument.setCreationDate(ZonedDateTime.now());
        buc.setDocuments(List.of(dokument));

        return buc;
    }

    @Test
    void kontrollerIdentifisertPerson_personSamstemmerMedSed_identifisert() {
        when(personFasade.hentPerson(aktørID)).thenReturn(personBuilder.build());
        assertThat(identifiseringKontrollService.kontrollerIdentifisertPerson(aktørID, rinaSaksnummer))
            .extracting(IdentifiseringsKontrollResultat::erIdentifisert, IdentifiseringsKontrollResultat::getBegrunnelser)
            .containsExactly(true, Collections.emptyList());
    }

    @Test
    void kontrollerIdentifisertPerson_personHarIkkeRiktigStatsborgerskap_ikkeIdentifisert() {
        when(personFasade.hentPerson(aktørID)).thenReturn(personBuilder.statsborgerskapLandkodeISO2(Set.of("DK")).build());
        assertThat(identifiseringKontrollService.kontrollerIdentifisertPerson(aktørID, rinaSaksnummer))
            .extracting(IdentifiseringsKontrollResultat::erIdentifisert, IdentifiseringsKontrollResultat::getBegrunnelser)
            .containsExactly(false, List.of(IdentifiseringsKontrollBegrunnelse.STATSBORGERSKAP));
    }

    @Test
    void kontrollerIdentifisertPerson_personHarFeilKjønn_ikkeIdentifisert() {
        when(personFasade.hentPerson(aktørID)).thenReturn(personBuilder.kjønn(Kjønn.MANN).build());
        assertThat(identifiseringKontrollService.kontrollerIdentifisertPerson(aktørID, rinaSaksnummer))
            .extracting(IdentifiseringsKontrollResultat::erIdentifisert, IdentifiseringsKontrollResultat::getBegrunnelser)
            .containsExactly(false, List.of(IdentifiseringsKontrollBegrunnelse.KJØNN));
    }

    @Test
    void kontrollerIdentifisertPerson_personHarIkkeRiktigFødselsdato_ikkeIdentifisert() {
        when(personFasade.hentPerson(aktørID)).thenReturn(personBuilder.fødselsdato(LocalDate.now().minusYears(3)).build());
        assertThat(identifiseringKontrollService.kontrollerIdentifisertPerson(aktørID, rinaSaksnummer))
            .extracting(IdentifiseringsKontrollResultat::erIdentifisert, IdentifiseringsKontrollResultat::getBegrunnelser)
            .containsExactly(false, List.of(IdentifiseringsKontrollBegrunnelse.FØDSELSDATO));
    }

    @Test
    void kontrollerIdentifisertPerson_personHarIkkeRiktigUtenlandskId_ikkeIdentifisert() {
        when(personFasade.hentPerson(aktørID)).thenReturn(personBuilder.utenlandskId(Set.of(new UtenlandskId("feil-pin", avsenderLand))).build());
        assertThat(identifiseringKontrollService.kontrollerIdentifisertPerson(aktørID, rinaSaksnummer))
            .extracting(IdentifiseringsKontrollResultat::erIdentifisert, IdentifiseringsKontrollResultat::getBegrunnelser)
            .containsExactly(false, List.of(IdentifiseringsKontrollBegrunnelse.UTENLANDSK_ID));
    }
}
