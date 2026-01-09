package no.nav.melosys.eessi.identifisering;

import java.util.List;
import java.util.Optional;

import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Bruker;
import no.nav.melosys.eessi.models.sed.nav.Nav;
import no.nav.melosys.eessi.models.sed.nav.Person;
import no.nav.melosys.eessi.models.sed.nav.Pin;
import no.nav.melosys.eessi.service.personsok.PersonsokKriterier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SedIdentifiseringServiceTest {

    private final String norskIdent = "64068648643";

    @Mock
    private PersonSok personSok;
    @Mock
    private PersonSokMetrikker personSokMetrikker;
    @Mock
    private BucIdentifisertService bucIdentifisertService;

    private SedIdentifiseringService sedIdentifiseringService;

    @BeforeEach
    public void setup() {
        sedIdentifiseringService = new SedIdentifiseringService(personSok, personSokMetrikker, bucIdentifisertService);
    }

    @Test
    void identifiserPerson_sakEksisterer_hentPersonFraSak() {
        final var rinaSaksnummer = "123321";
        when(bucIdentifisertService.finnIdentifisertPerson(rinaSaksnummer)).thenReturn(Optional.of(norskIdent));
        Optional<String> res = sedIdentifiseringService.identifiserPerson(rinaSaksnummer, lagSED());
        assertThat(res).contains(norskIdent);
    }

    @Test
    void identifiserPerson_ingenSakSedMedNorskIdent_personSuksessfultValidert() {
        SED sed = lagSED();
        sed.getNav().getBruker().getPerson().setPin(List.of(new Pin(norskIdent, "NO", null)));

        when(personSok.vurderPerson(eq(norskIdent), any(PersonsokKriterier.class))).thenReturn(PersonSokResultat.identifisert(norskIdent, false));
        Optional<String> res = sedIdentifiseringService.identifiserPerson("123", sed);
        assertThat(res).contains(norskIdent);
    }

    @Test
    void identifiserPerson_ingenSakSedIngenNorskIdent_finnerIkkePersonFraSedFinnerFraSøk() {
        when(personSok.søkEtterPerson(any(PersonsokKriterier.class))).thenReturn(PersonSokResultat.identifisert(norskIdent, false));
        Optional<String> res = sedIdentifiseringService.identifiserPerson("123", lagSED());
        assertThat(res).contains(norskIdent);
    }

    @Test
    void identifiserPerson_ingenSakSedIngenNorskIdent_finnerIkkePersonFraSedFinnerIkkeFraSøk() {
        when(personSok.søkEtterPerson(any(PersonsokKriterier.class))).thenReturn(PersonSokResultat.ikkeIdentifisert(SoekBegrunnelse.FLERE_TREFF));
        Optional<String> res = sedIdentifiseringService.identifiserPerson("123", lagSED());
        assertThat(res).isNotPresent();
    }

    @Test
    void identifiserPerson_erXSEDUtenPerson_ingenTreff() {
        SED xSED = lagXSEDUtenBruker();
        Optional<String> res = sedIdentifiseringService.identifiserPerson("123", xSED);
        verify(personSokMetrikker).counter(SoekBegrunnelse.INGEN_PERSON_I_SED);
        assertThat(res).isNotPresent();
    }

    private SED lagSED() {
        SED sed = new SED();
        sed.setNav(new Nav());
        sed.getNav().setBruker(new Bruker());
        sed.getNav().getBruker().setPerson(new Person());
        sed.getNav().getBruker().getPerson().setFoedselsdato("2000-01-01");
        sed.getNav().getBruker().getPerson().setStatsborgerskap(List.of());
        sed.getNav().getBruker().getPerson().setPin(List.of());

        sed.setSedType(SedType.A001.name());
        return sed;
    }

    private SED lagXSEDUtenBruker() {
        SED sed = new SED();
        sed.setNav(new Nav());
        sed.getNav().setSak(new no.nav.melosys.eessi.models.sed.nav.Sak());
        sed.setSedType(SedType.X007.name());
        return sed;
    }
}
