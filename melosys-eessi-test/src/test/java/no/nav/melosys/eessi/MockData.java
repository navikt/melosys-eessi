package no.nav.melosys.eessi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.integration.pdl.dto.*;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.buc.*;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003;
import no.nav.melosys.eessi.models.sed.nav.*;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import org.springframework.util.StringUtils;

import static no.nav.melosys.eessi.models.BucType.LA_BUC_02;
import static no.nav.melosys.eessi.models.SedType.A003;

public class MockData {
    public MockData() {
    }

    SedHendelse sedHendelse(String rinaSaksnummer, String sedID, String ident) {
        return SedHendelse.builder()
                .avsenderId("US:avsenderId")
                .avsenderNavn("avsender")
                .bucType(LA_BUC_02.name())
                .id(42L)
                .mottakerId("mottakerId")
                .mottakerNavn("mottakerNavn")
                .navBruker(ident)
                .rinaDokumentId("1")
                .rinaSakId(rinaSaksnummer)
                .rinaDokumentVersjon("1")
                .sedType(A003.name())
                .sedId(sedID)
                .sektorKode("LA")
                .build();
    }

    SED sed(LocalDate fødselsdato, String statsborgerskap, String ident) {
        SED sed = new SED();
        sed.setSedType(A003.name());

        MedlemskapA003 medlemskap = new MedlemskapA003();
        medlemskap.setRelevantartikkelfor8832004eller9872009("13_1_a");
        medlemskap.setDeterminationProvisional("nei");

        PeriodeA010 periode = new PeriodeA010();
        periode.setStartdato("2019-06-01");
        periode.setSluttdato("2019-12-01");

        VedtakA003 vedtak = new VedtakA003();
        vedtak.setLand("SE");
        vedtak.setGjelderperiode(periode);
        vedtak.setEropprinneligvedtak("ja");
        medlemskap.setVedtak(vedtak);
        sed.setMedlemskap(medlemskap);

        Nav nav = new Nav();
        Person person = new Person();
        person.setFoedselsdato(fødselsdato.toString());
        person.setKjoenn(Kjønn.M);
        Statsborgerskap statsborgerskap1 = new Statsborgerskap();
        statsborgerskap1.setLand(statsborgerskap);
        person.setStatsborgerskap(Collections.singletonList(statsborgerskap1));
        person.setPin(StringUtils.hasText(ident) ? List.of(new Pin(ident, "NO", null)) : Collections.emptyList());
        Bruker bruker = new Bruker();
        bruker.setPerson(person);
        nav.setBruker(bruker);
        sed.setNav(nav);

        return sed;
    }

    SED sedUkjentPin(LocalDate fødselsdato, String statsborgerskap, String ident) {
        SED sed = new SED();
        sed.setSedType(A003.name());

        MedlemskapA003 medlemskap = new MedlemskapA003();
        medlemskap.setRelevantartikkelfor8832004eller9872009("13_1_a");
        medlemskap.setDeterminationProvisional("nei");

        PeriodeA010 periode = new PeriodeA010();
        periode.setStartdato("2019-06-01");
        periode.setSluttdato("2019-12-01");

        VedtakA003 vedtak = new VedtakA003();
        vedtak.setLand("SE");
        vedtak.setGjelderperiode(periode);
        vedtak.setEropprinneligvedtak("ja");
        medlemskap.setVedtak(vedtak);
        sed.setMedlemskap(medlemskap);

        Nav nav = new Nav();
        Person person = new Person();
        person.setFoedselsdato(fødselsdato.toString());
        person.setKjoenn(Kjønn.M);
        Statsborgerskap statsborgerskap1 = new Statsborgerskap();
        statsborgerskap1.setLand(statsborgerskap);
        person.setStatsborgerskap(Collections.singletonList(statsborgerskap1));
        person.setPin(StringUtils.hasText(ident) ? List.of(new Pin(ident, "NO", null)) : Collections.emptyList());
        Bruker bruker = new Bruker();
        bruker.setPerson(person);
        nav.setBruker(bruker);
        sed.setNav(nav);

        return sed;
    }


    OpprettJournalpostResponse journalpostResponse(boolean ferdigstilt) {
        return OpprettJournalpostResponse.builder()
                .journalpostId("1")
                .dokumenter(Collections.singletonList(new OpprettJournalpostResponse.Dokument("1")))
                .journalstatus(ferdigstilt ? "ENDELIG" : "MIDLERTIDIG")
                .melding("OK")
                .build();
    }

    Sak sak(String aktoerId) {
        return new Sak("1", "GEN", "MELOSYS", aktoerId, "012356745", "1", "Tester", ZonedDateTime.now());
    }

    SedMedVedlegg sedMedVedlegg() {
        return new SedMedVedlegg(new SedMedVedlegg.BinaerFil("fil123", "", new byte[0]), Collections.emptyList());
    }

    BUC buc(String id) {
        // Create a document with conversations
        Document document = new Document();
        document.setConversations(Arrays.asList(new Conversation(), new Conversation()));
        document.setId(id);
        document.setStatus("CREATED");
        document.setDirection("IN");
        List<Document> documents = Collections.singletonList(document);

        // Create organisation and creator
        Organisation organisation = new Organisation();
        organisation.setCountryCode("SE");

        Creator creator = new Creator();
        creator.setOrganisation(organisation);

        // Construct the BUC object with documents and creator
        return new BUC(
            id, // id
            null, // startDate
            null, // lastUpdate
            null, // status
            creator, // creator
            documents, // documents
            Collections.emptyList(), // actions
            null, // bucType
            null, // bucVersion
            Collections.emptyList(), // participants
            null // internationalId
        );
    }
    public PDLPerson pdlPerson(LocalDate fødselsdato, String statsborgerskapLandkode) {

        var pdlNavn = new PDLNavn();
        pdlNavn.setFornavn("NyttFornavn");
        pdlNavn.setEtternavn("NyttEtternavn");
        pdlNavn.setMetadata(new PDLMetadata());
        pdlNavn.getMetadata().setEndringer(Set.of(
                new PDLEndring("OPPRETT", LocalDateTime.of(2010, 1, 1, 0, 0)),
                new PDLEndring("KORRIGER", LocalDateTime.of(2020, 1, 1, 0, 0))
        ));

        var pdlFødsel = new PDLFoedsel();
        pdlFødsel.setFoedselsdato(fødselsdato);
        pdlFødsel.setMetadata(new PDLMetadata());
        pdlFødsel.getMetadata().setEndringer(Set.of(
                new PDLEndring("OPPRETT", LocalDateTime.of(1990, 1, 1, 0, 0))
        ));

        var pdlStatsborgerskap = new PDLStatsborgerskap();
        pdlStatsborgerskap.setLand(statsborgerskapLandkode);

        var pdlPersonstatus = new PDLFolkeregisterPersonstatus();
        pdlPersonstatus.setStatus("bosatt");
        pdlPersonstatus.setMetadata(new PDLMetadata());
        pdlPersonstatus.getMetadata().setEndringer(Set.of(
                new PDLEndring("OPPRETT", LocalDateTime.of(2009, 1, 1, 0, 0))
        ));

        var pdlKjønn = new PDLKjoenn();
        pdlKjønn.setKjoenn(PDLKjoennType.MANN);
        pdlKjønn.setMetadata(new PDLMetadata());
        pdlKjønn.getMetadata().setEndringer(Set.of(
            new PDLEndring("OPPRETT", LocalDateTime.of(2009, 1, 1, 0, 0))
        ));

        var pdlUtenlandskId = new PDLUtenlandskIdentifikator();
        pdlUtenlandskId.setIdentifikasjonsnummer("Ikke-brukt-enda-123");
        pdlUtenlandskId.setUtstederland("SE");

        return new PDLPerson(
            List.of(pdlNavn) ,
            List.of(pdlFødsel),
            List.of(pdlStatsborgerskap),
            List.of(pdlPersonstatus),
            List.of(pdlUtenlandskId),
            List.of(pdlKjønn)
        );
    }


    PDLIdentliste lagPDLIdentListe(String ident, String aktørID) {
        var pdlFolkeregisterIdent = new PDLIdent();
        pdlFolkeregisterIdent.setIdent(ident);
        pdlFolkeregisterIdent.setGruppe(PDLIdentGruppe.FOLKEREGISTERIDENT);

        var pdlAktørId = new PDLIdent();
        pdlAktørId.setIdent(aktørID);
        pdlAktørId.setGruppe(PDLIdentGruppe.AKTORID);

        var pdlIdentliste = new PDLIdentliste();
        pdlIdentliste.setIdenter(Set.of(pdlFolkeregisterIdent, pdlAktørId));
        return pdlIdentliste;
    }

}
