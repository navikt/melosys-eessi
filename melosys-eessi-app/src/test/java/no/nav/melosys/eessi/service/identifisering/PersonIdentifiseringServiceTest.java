package no.nav.melosys.eessi.service.identifisering;

import java.util.List;
import java.util.Optional;

import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.metrikker.PersonSokMetrikker;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Bruker;
import no.nav.melosys.eessi.models.sed.nav.Nav;
import no.nav.melosys.eessi.models.sed.nav.Person;
import no.nav.melosys.eessi.models.sed.nav.Pin;
import no.nav.melosys.eessi.service.sak.SakService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import no.nav.melosys.eessi.service.tps.personsok.PersonsoekKriterier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonIdentifiseringServiceTest {

    @Mock
    private PersonSok personSok;
    @Mock
    private PDLPersonSok pdlPersonSok;
    @Mock
    private SaksrelasjonService saksrelasjonService;
    @Mock
    private SakService sakService;
    @Mock
    private PersonFasade personFasade;
    @Mock
    private PersonSokMetrikker personSokMetrikker;

    private PersonIdentifiseringService personIdentifiseringService;

    @BeforeEach
    public void setup() {
        personIdentifiseringService = new PersonIdentifiseringService(
                personSok, pdlPersonSok, saksrelasjonService, sakService, personFasade, personSokMetrikker
        );
    }

    @Test
    void identifiserPerson_sakEksisterer_hentPersonFraSak() {
        final String norskIdent = "333333";
        Sak sak = new Sak();
        sak.setAktoerId("32132132");

        FagsakRinasakKobling fagsakRinasakKobling = new FagsakRinasakKobling();
        fagsakRinasakKobling.setGsakSaksnummer(123L);

        when(personFasade.hentNorskIdent(anyString())).thenReturn(norskIdent);
        when(sakService.hentsak(anyLong())).thenReturn(sak);
        when(saksrelasjonService.finnVedRinaSaksnummer(anyString())).thenReturn(Optional.of(fagsakRinasakKobling));

        Optional<String> res = personIdentifiseringService.identifiserPerson("123", lagSED());
        assertThat(res).contains(norskIdent);
    }

    @Test
    void identifiserPerson_ingenSakSedMedNorskIdent_personSuksessfultValidert() {
        final String norskIdent = "333333";
        SED sed = lagSED();
        sed.getNav().getBruker().getPerson().setPin(List.of(new Pin(norskIdent, "NO", null)));

        when(personSok.vurderPerson(eq(norskIdent), any(PersonsoekKriterier.class))).thenReturn(PersonSokResultat.identifisert(norskIdent));
        Optional<String> res = personIdentifiseringService.identifiserPerson("123", sed);
        assertThat(res).contains(norskIdent);
    }

    @Test
    void identifiserPerson_ingenSakSedIngenNorskIdent_finnerIkkePersonFraSedFinnerFraSøk() {
        final String norskIdent = "33";
        when(personSok.søkEtterPerson(any(PersonsoekKriterier.class))).thenReturn(PersonSokResultat.identifisert(norskIdent));
        Optional<String> res = personIdentifiseringService.identifiserPerson("123", lagSED());
        assertThat(res).contains(norskIdent);
    }

    @Test
    void identifiserPerson_ingenSakSedIngenNorskIdent_finnerIkkePersonFraSedFinnerIkkeFraSøk() {
        when(personSok.søkEtterPerson(any(PersonsoekKriterier.class))).thenReturn(PersonSokResultat.ikkeIdentifisert(SoekBegrunnelse.FLERE_TREFF));
        Optional<String> res = personIdentifiseringService.identifiserPerson("123", lagSED());
        assertThat(res).isNotPresent();
    }

    @Test
    void identifiserPerson_erXSEDUtenPerson_ingenTreff() {
        SED xSED = lagXSEDUtenBruker();
        Optional<String> res = personIdentifiseringService.identifiserPerson("123", xSED);
        verify(personSokMetrikker).counter(eq(SoekBegrunnelse.INGEN_PERSON_I_SED));
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
