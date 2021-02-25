package no.nav.melosys.eessi.integration.pdl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import no.nav.melosys.eessi.integration.pdl.dto.*;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.service.tps.personsok.PersonSoekResponse;
import no.nav.melosys.eessi.service.tps.personsok.PersonsoekKriterier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PDLServiceTest {

    @Mock
    private PDLConsumer pdlConsumer;

    private PDLService pdlService;

    @BeforeEach
    public void setup() {
        pdlService = new PDLService(pdlConsumer);
    }

    @Test
    void hentPerson_personMedFlereEndringerPåNavnFlereStatsborgerskap_nyesteEndringerForNavnAlleStatsborgerskapMappes() {
        final String ident = "12345600000";

        when(pdlConsumer.hentPerson(eq(ident))).thenReturn(lagPersonMedFlereEndringer());
        assertThat(pdlService.hentPerson(ident))
                .extracting(
                        PersonModell::getIdent,
                        PersonModell::getFornavn,
                        PersonModell::getEtternavn,
                        PersonModell::getFødselsdato,
                        PersonModell::getStatsborgerskapLandkodeISO2,
                        PersonModell::isErOpphørt)
                .containsExactly(
                        ident,
                        "NyttFornavn",
                        "NyttEtternavn",
                        LocalDate.of(1990, 1, 1),
                        Set.of("NO", "SE", "PL"),
                        false);
    }

    private PDLPerson lagPersonMedFlereEndringer() {
        var pdlPerson = new PDLPerson();

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

        pdlPerson.setNavn(Set.of(gammeltPdlNavn, nyttPdlNavn));
        pdlPerson.setFoedsel(Set.of(pdlFødsel));
        pdlPerson.setStatsborgerskap(Set.of(norskStatsborgerskap, svenskStatsborgerskap, polskStatsborgerskap));
        pdlPerson.setFolkeregisterpersonstatus(Set.of(pdlPersonstatus));
        return pdlPerson;
    }

    @Test
    void soekEtterPerson_finnerToPersoner_mapperToIdenter() {
        ArgumentCaptor<PDLSokRequestVars> captor = ArgumentCaptor.forClass(PDLSokRequestVars.class);
        when(pdlConsumer.søkPerson(any())).thenReturn(lagSøkPersonResponse());

        PersonsoekKriterier personsoekKriterier = PersonsoekKriterier.builder()
                .fornavn("fornavn")
                .etternavn("etternavn")
                .foedselsdato(LocalDate.of(2000, 1, 1))
                .build();

        var response = pdlService.soekEtterPerson(personsoekKriterier);
        assertThat(response).hasSize(2)
                .flatExtracting(PersonSoekResponse::getIdent)
                .containsExactlyInAnyOrder("1", "2");

        verify(pdlConsumer).søkPerson(captor.capture());
        var pdlRequestDto = captor.getValue();
        assertThat(pdlRequestDto.getPaging())
                .extracting(PDLPaging::getPageNumber, PDLPaging::getResultsPerPage)
                .containsExactly(1, 20);
        assertThat(pdlRequestDto.getCriteria())
                .hasSize(3)
                .flatExtracting(PDLSokCriteria::getFeltNavn)
                .containsExactlyInAnyOrder(
                        "person.navn.fornavn",
                        "person.navn.etternavn",
                        "person.foedsel.foedselsdato"
                );
    }

    PDLSokPerson lagSøkPersonResponse() {
        PDLSokPerson pdlSokPerson = new PDLSokPerson();

        PDLSokHits treff1 = new PDLSokHits();
        treff1.setIdenter(
                Set.of(new PDLIdent("FOLKEREGISTERIDENT", "1"), new PDLIdent("AKTORID", "00"))
        );

        PDLSokHits treff2 = new PDLSokHits();
        treff2.setIdenter(
                Set.of(new PDLIdent("FOLKEREGISTERIDENT", "2"), new PDLIdent("NPID", "99"))
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
        identliste.getIdenter().add(new PDLIdent("AKTORID", "11111"));
        identliste.getIdenter().add(new PDLIdent("FOLKEREGISTERIDENT", "22222"));
        identliste.getIdenter().add(new PDLIdent("NPID", "33333"));

        return identliste;
    }

    private PDLIdentliste lagTomIdentliste() {
        var identliste = new PDLIdentliste();
        identliste.setIdenter(Collections.emptySet());
        return identliste;
    }
}
