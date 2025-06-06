package no.nav.melosys.eessi.integration.pdl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import no.nav.melosys.eessi.integration.pdl.dto.*;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.person.Kjønn;
import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.models.person.UtenlandskId;
import no.nav.melosys.eessi.service.personsok.PersonSokResponse;
import no.nav.melosys.eessi.service.personsok.PersonsokKriterier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static no.nav.melosys.eessi.integration.pdl.dto.PDLIdentGruppe.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PDLServiceTest {

    @Mock
    private PDLConsumer pdlConsumer;

    @Mock
    private PdlWebConsumer pdlWebConsumer;

    private PDLService pdlService;

    @BeforeEach
    void setup() {
        pdlService = new PDLService(pdlConsumer, pdlWebConsumer);
    }

    @Test
    void hentPerson_personMedFlereEndringerPåNavnFlereStatsborgerskap_nyesteEndringerForNavnAlleStatsborgerskapMappes() {
        final String ident = "12345600000";

        when(pdlConsumer.hentPerson(ident)).thenReturn(lagPersonMedFlereEndringer());
        assertThat(pdlService.hentPerson(ident))
            .extracting(
                PersonModell::getIdent,
                PersonModell::getFornavn,
                PersonModell::getEtternavn,
                PersonModell::getFødselsdato,
                PersonModell::getStatsborgerskapLandkodeISO2,
                PersonModell::getErOpphørt,
                PersonModell::getUtenlandskId,
                PersonModell::getKjønn)
            .containsExactly(
                ident,
                "NyttFornavn",
                "NyttEtternavn",
                LocalDate.of(1990, 1, 1),
                Set.of("NO", "SE", "PL"),
                false,
                Set.of(new UtenlandskId("2222-1111", "SE")),
                Kjønn.KVINNE
            );
    }

    private PDLPerson lagPersonMedFlereEndringer() {

        var gammeltPdlNavn = new PDLNavn();
        gammeltPdlNavn.setFornavn("GammeltFornavn");
        gammeltPdlNavn.setEtternavn("GammeltEtternavn");
        gammeltPdlNavn.setMetadata(new PDLMetadata());
        gammeltPdlNavn.getMetadata().setEndringer(Set.of(
            new PDLEndring("OPPRETT", LocalDateTime.of(2015, 1, 1, 0, 0))
        ));

        var nyttPdlNavn = new PDLNavn();
        nyttPdlNavn.setFornavn("NyttFornavn");
        nyttPdlNavn.setEtternavn("NyttEtternavn");
        nyttPdlNavn.setMetadata(new PDLMetadata());
        nyttPdlNavn.getMetadata().setEndringer(Set.of(
            new PDLEndring("OPPRETT", LocalDateTime.of(2010, 1, 1, 0, 0)),
            new PDLEndring("KORRIGER", LocalDateTime.of(2020, 1, 1, 0, 0))
        ));

        var pdlFødsel = new PDLFoedsel();
        pdlFødsel.setFoedselsdato(LocalDate.of(1990, 1, 1));
        pdlFødsel.setMetadata(new PDLMetadata());
        pdlFødsel.getMetadata().setEndringer(Set.of(
            new PDLEndring("OPPRETT", LocalDateTime.of(1990, 1, 1, 0, 0))
        ));

        var norskStatsborgerskap = new PDLStatsborgerskap();
        norskStatsborgerskap.setLand("NOR");

        var svenskStatsborgerskap = new PDLStatsborgerskap();
        svenskStatsborgerskap.setLand("SWE");

        var polskStatsborgerskap = new PDLStatsborgerskap();
        polskStatsborgerskap.setLand("POL");

        var pdlPersonstatus = new PDLFolkeregisterPersonstatus();
        pdlPersonstatus.setStatus("bosatt");
        pdlPersonstatus.setMetadata(new PDLMetadata());
        pdlPersonstatus.getMetadata().setEndringer(Set.of(
            new PDLEndring("OPPRETT", LocalDateTime.of(2009, 1, 1, 0, 0))
        ));

        var pdlUtenlandskIdentifikator = new PDLUtenlandskIdentifikator();
        pdlUtenlandskIdentifikator.setIdentifikasjonsnummer("2222-1111");
        pdlUtenlandskIdentifikator.setUtstederland("SWE");

        var pdlKjønn = new PDLKjoenn();
        pdlKjønn.setKjoenn(PDLKjoennType.KVINNE);
        pdlKjønn.setMetadata(new PDLMetadata());
        pdlKjønn.getMetadata().setEndringer(Set.of(
            new PDLEndring("OPPRETT", LocalDateTime.of(2009, 1, 1, 0, 0)))
        );

        return new PDLPerson(
            List.of(gammeltPdlNavn, nyttPdlNavn) ,
            List.of(pdlFødsel),
            List.of(norskStatsborgerskap, svenskStatsborgerskap, polskStatsborgerskap),
            List.of(pdlPersonstatus),
            List.of(pdlUtenlandskIdentifikator),
            List.of(pdlKjønn)
        );
    }

    @Test
    void soekEtterPerson_finnerIkkeFolkeregisterIdent_returnererTomListe() {
        when(pdlConsumer.søkPerson(any())).thenReturn(lagSøkPersonResponseUtenFolkeregisterIdent());
        var personsokKriterier = PersonsokKriterier.builder()
            .fornavn("fornavn")
            .etternavn("etternavn")
            .foedselsdato(LocalDate.of(2000, 1, 1))
            .build();


        var response = pdlService.soekEtterPerson(personsokKriterier);


        assertThat(response).isEmpty();
    }

    PDLSokPerson lagSøkPersonResponseUtenFolkeregisterIdent() {
        PDLSokPerson pdlSokPerson = new PDLSokPerson();

        PDLSokHit treff = new PDLSokHit();
        treff.setIdenter(Set.of(new PDLIdent(NPID, "1"), new PDLIdent(AKTORID, "2")));

        pdlSokPerson.setHits(Set.of(treff));
        return pdlSokPerson;
    }

    @Test
    void soekEtterPerson_finnerToPersoner_mapperToIdenter() {
        ArgumentCaptor<PDLSokRequestVars> captor = ArgumentCaptor.forClass(PDLSokRequestVars.class);
        when(pdlConsumer.søkPerson(any())).thenReturn(lagSøkPersonResponse());

        PersonsokKriterier personsokKriterier = PersonsokKriterier.builder()
            .fornavn("fornavn")
            .etternavn("etternavn")
            .foedselsdato(LocalDate.of(2000, 1, 1))
            .build();

        var response = pdlService.soekEtterPerson(personsokKriterier);
        assertThat(response).hasSize(2)
            .flatExtracting(PersonSokResponse::getIdent)
            .containsExactlyInAnyOrder("1", "2");

        verify(pdlConsumer).søkPerson(captor.capture());
        var pdlRequestDto = captor.getValue();
        assertThat(pdlRequestDto.getPaging())
            .extracting(PDLPaging::getPageNumber, PDLPaging::getResultsPerPage)
            .containsExactly(1, 20);
        assertThat(pdlRequestDto.getCriteria())
            .hasSize(3)
            .flatExtracting(PDLSokCriterion::getFieldName)
            .containsExactlyInAnyOrder(
                "person.navn.fornavn",
                "person.navn.etternavn",
                "person.foedselsdato.foedselsdato"
            );
    }

    PDLSokPerson lagSøkPersonResponse() {
        PDLSokPerson pdlSokPerson = new PDLSokPerson();

        PDLSokHit treff1 = new PDLSokHit();
        treff1.setIdenter(
            Set.of(new PDLIdent(FOLKEREGISTERIDENT, "1"), new PDLIdent(AKTORID, "00"))
        );

        PDLSokHit treff2 = new PDLSokHit();
        treff2.setIdenter(
            Set.of(new PDLIdent(FOLKEREGISTERIDENT, "2"), new PDLIdent(NPID, "99"))
        );

        pdlSokPerson.setHits(Set.of(treff1, treff2));
        return pdlSokPerson;
    }

    @Test
    void hentAktørID_finnes_verifiserAktørId() {
        when(pdlConsumer.hentIdenter(anyString())).thenReturn(lagIdentliste());
        assertThat(pdlService.hentAktoerId("123")).isEqualTo("11111");
    }

    @Test
    void hentAktørID_finnesIkke_kasterFeil() {
        when(pdlConsumer.hentIdenter(anyString())).thenReturn(lagTomIdentliste());
        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> pdlService.hentAktoerId("123"))
            .withMessageContaining("Finner ikke aktørID");
    }

    @Test
    void hentNorskIdent_finnes_verifiserIdent() {
        when(pdlConsumer.hentIdenter(anyString())).thenReturn(lagIdentliste());
        assertThat(pdlService.hentNorskIdent("123")).isEqualTo("22222");
    }

    @Test
    void hentNorskIdent_finnesIkke_kasterFeil() {
        when(pdlConsumer.hentIdenter(anyString())).thenReturn(lagTomIdentliste());
        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> pdlService.hentNorskIdent("123"))
            .withMessageContaining("Finner ikke folkeregisterident");
    }

    private PDLIdentliste lagIdentliste() {
        var identliste = new PDLIdentliste();
        identliste.setIdenter(new HashSet<>());
        identliste.getIdenter().add(new PDLIdent(AKTORID, "11111"));
        identliste.getIdenter().add(new PDLIdent(FOLKEREGISTERIDENT, "22222"));
        identliste.getIdenter().add(new PDLIdent(NPID, "33333"));

        return identliste;
    }

    private PDLIdentliste lagTomIdentliste() {
        var identliste = new PDLIdentliste();
        identliste.setIdenter(Collections.emptySet());
        return identliste;
    }
}
