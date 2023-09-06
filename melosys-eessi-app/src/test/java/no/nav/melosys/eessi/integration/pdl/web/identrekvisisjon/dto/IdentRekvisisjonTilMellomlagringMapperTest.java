package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import no.nav.melosys.eessi.integration.pdl.dto.PDLKjoennType;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IdentRekvisisjonTilMellomlagringMapperTest {


    @Test
    public void byggIdentRekvisisjonTilMellomlagringSetterRiktigPersonopplysninger() {
        SED sed = lagSED();
        SedMottattHendelse sedMottattHendelse = createSedHendelse();
        IdentRekvisisjonTilMellomlagring result = IdentRekvisisjonTilMellomlagringMapper.byggIdentRekvisisjonTilMellomlagring(sedMottattHendelse, sed);

        assertEquals("TestFornavn", result.getPersonopplysninger().getFornavn());
        assertEquals("TestEtternavn", result.getPersonopplysninger().getEtternavn());
        assertEquals(LocalDate.of(2000, 1, 1), result.getPersonopplysninger().getFoedselsdato());
        assertEquals(Set.of("SE", "NO"), result.getPersonopplysninger().getStatsborgerskap());
        assertEquals("Miami", result.getPersonopplysninger().getFoedested());
        assertEquals("USA", result.getPersonopplysninger().getFoedeland());
    }

    @Test
    public void byggIdentRekvisisjonTilMellomlagringSetterRiktigKilde() {
        SED sed = lagSED();
        SedMottattHendelse sedMottattHendelse = createSedHendelse();
        IdentRekvisisjonTilMellomlagring result = IdentRekvisisjonTilMellomlagringMapper.byggIdentRekvisisjonTilMellomlagring(sedMottattHendelse, sed);

        assertEquals("SE:123", result.getKilde().getInstitusjon());
        assertEquals("SE", result.getKilde().getLandkode());
    }

    @Test
    public void byggIdentRekvisisjonTilMellomlagringSetterRiktigKontaktadresse() {
        SED sed = lagSED();
        SedMottattHendelse sedMottattHendelse = createSedHendelse();
        IdentRekvisisjonTilMellomlagring result = IdentRekvisisjonTilMellomlagringMapper.byggIdentRekvisisjonTilMellomlagring(sedMottattHendelse, sed);

        assertEquals("TestLand", result.getKontaktadresse().getUtenlandskVegadresse().getLandkode());
        assertEquals("TestBy", result.getKontaktadresse().getUtenlandskVegadresse().getBySted());
        assertEquals("TestPostnummer", result.getKontaktadresse().getUtenlandskVegadresse().getPostkode());
        assertEquals("TestRegion", result.getKontaktadresse().getUtenlandskVegadresse().getRegionDistriktOmraade());
        assertEquals("TestGate", result.getKontaktadresse().getUtenlandskVegadresse().getAdressenavnNummer());
        assertEquals("TestBygning", result.getKontaktadresse().getUtenlandskVegadresse().getBygningEtasjeLeilighet());
    }

    @Test
    public void byggIdentRekvisisjonTilMellomlagringSetterUtenlandskIdentifikasjon() {
        SED sed = lagSED();
        SedMottattHendelse sedMottattHendelse = createSedHendelse();
        IdentRekvisisjonTilMellomlagring result = IdentRekvisisjonTilMellomlagringMapper.byggIdentRekvisisjonTilMellomlagring(sedMottattHendelse, sed);

        assertEquals("12345678911", result.getUtenlandskIdentifikasjon().getUtenlandskId());
        assertEquals("SE", result.getUtenlandskIdentifikasjon().getUtstederland());
    }

    @Test
    public void byggIdentRekvisisjonTilMellomlagringSetterIkkeUtenlandskIdentifikasjon() {
        SED sed = lagSED();
        sed.getNav().getBruker().getPerson().setPin(List.of(new Pin("12345678911", "DK", "sektor")));
        SedMottattHendelse sedMottattHendelse = createSedHendelse();
        IdentRekvisisjonTilMellomlagring result = IdentRekvisisjonTilMellomlagringMapper.byggIdentRekvisisjonTilMellomlagring(sedMottattHendelse, sed);

        assertNull(result.getUtenlandskIdentifikasjon());
    }

    @Test
    public void byggIdentRekvisisjonTilMellomlagring_setterKjonnTilMann() {
        SED sed = lagSED();
        sed.getNav().getBruker().getPerson().setKjoenn(Kjønn.M);
        IdentRekvisisjonTilMellomlagring result = IdentRekvisisjonTilMellomlagringMapper.byggIdentRekvisisjonTilMellomlagring(createSedHendelse(), sed);

        assertEquals(PDLKjoennType.MANN, result.getPersonopplysninger().getKjoenn());
    }

    @Test
    public void byggIdentRekvisisjonTilMellomlagring_setterKjonnTilKvinne() {
        SED sed = lagSED();
        sed.getNav().getBruker().getPerson().setKjoenn(Kjønn.K);
        IdentRekvisisjonTilMellomlagring result = IdentRekvisisjonTilMellomlagringMapper.byggIdentRekvisisjonTilMellomlagring(createSedHendelse(), sed);

        assertEquals(PDLKjoennType.KVINNE, result.getPersonopplysninger().getKjoenn());
    }

    @Test
    public void byggIdentRekvisisjonTilMellomlagring_setterKjonnTilUkjent() {
        SED sed = lagSED();
        sed.getNav().getBruker().getPerson().setKjoenn(Kjønn.U);
        IdentRekvisisjonTilMellomlagring result = IdentRekvisisjonTilMellomlagringMapper.byggIdentRekvisisjonTilMellomlagring(createSedHendelse(), sed);

        assertEquals(PDLKjoennType.UKJENT, result.getPersonopplysninger().getKjoenn());
    }

    private SED lagSED() {
        Person person = new Person();
        person.setFornavn("TestFornavn");
        person.setEtternavn("TestEtternavn");
        person.setFoedselsdato("2000-01-01");
        person.setFoedested(new Foedested());
        person.getFoedested().setLand("USA");
        person.getFoedested().setBy("Miami");
        person.setStatsborgerskap(List.of(new Statsborgerskap("NO"), new Statsborgerskap("SE")));
        person.setPin(List.of(new Pin("12345678911", "SE", "sektor")));
        Adresse adresse = new Adresse();
        adresse.setBy("TestBy");
        adresse.setLand("TestLand");
        adresse.setGate("TestGate");
        adresse.setPostnummer("TestPostnummer");
        adresse.setRegion("TestRegion");
        adresse.setBygning("TestBygning");
        Bruker bruker = new Bruker();
        bruker.setPerson(person);
        bruker.setAdresse(List.of(adresse));
        Sak sak = new Sak();
        Nav nav = new Nav();
        nav.setBruker(bruker);
        nav.setSak(sak);
        SED sed = new SED();
        sed.setNav(nav);

        sed.setSedType(SedType.A001.name());
        return sed;
    }

    static SedMottattHendelse createSedHendelse() {
        return SedMottattHendelse.builder()
            .sedHendelse(SedHendelse.builder()
                .navBruker("navbruker")
                .rinaDokumentId("rinadok")
                .rinaSakId("rinasak")
                .avsenderId("SE:123")
                .avsenderNavn("Sweden")
                .bucType("buc")
                .sedType("A001")
                .id(1L)
                .build())
            .build();
    }

}
