package no.nav.melosys.eessi.integration.pdl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import no.nav.melosys.eessi.integration.pdl.dto.*;
import no.nav.melosys.eessi.models.person.PersonModell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
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
}
