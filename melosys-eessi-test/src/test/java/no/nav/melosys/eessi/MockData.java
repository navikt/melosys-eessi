package no.nav.melosys.eessi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import no.nav.melosys.eessi.integration.dokkat.dto.DokumentTypeInfoDto;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.integration.pdl.dto.*;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Conversation;
import no.nav.melosys.eessi.models.buc.Document;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA002;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.UnntakA002;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.VedtakA002;
import no.nav.melosys.eessi.models.sed.nav.*;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import org.springframework.util.StringUtils;

import static no.nav.melosys.eessi.models.BucType.LA_BUC_01;
import static no.nav.melosys.eessi.models.SedType.A002;

public class MockData {
    public MockData() {
    }

    DokumentTypeInfoDto dokumentTypeInfoDto() {
        DokumentTypeInfoDto dokumentTypeInfoDto = new DokumentTypeInfoDto();
        dokumentTypeInfoDto.setDokumentKategori("dokumentKategori");
        dokumentTypeInfoDto.setDokumentTittel("dokumentTittel");
        dokumentTypeInfoDto.setDokumenttypeId("dokumenttypeId");
        dokumentTypeInfoDto.setBehandlingstema("behandlingstema");
        dokumentTypeInfoDto.setTema("tema");
        return dokumentTypeInfoDto;
    }

    SedHendelse sedHendelse(String rinaSaksnummer, String sedID, String ident) {
        return SedHendelse.builder()
                .avsenderId("avsenderId")
                .avsenderNavn("avsender")
                .bucType(LA_BUC_01.name())
                .id(42L)
                .mottakerId("mottakerId")
                .mottakerNavn("mottakerNavn")
                .navBruker(ident)
                .rinaDokumentId("1")
                .rinaSakId(rinaSaksnummer)
                .rinaDokumentVersjon("1")
                .sedType(A002.name())
                .sedId(sedID)
                .sektorKode("LA")
                .build();
    }

    SED sed(LocalDate fødselsdato, String statsborgerskap, String ident) {
        SED sed = new SED();
        sed.setSedType(A002.name());

        MedlemskapA002 medlemskap = new MedlemskapA002();
        UnntakA002 unntak = new UnntakA002();
        VedtakA002 vedtak = new VedtakA002();
        vedtak.setResultat("godkjent_for_annen_periode");
        vedtak.setBegrunnelse("begrunnelse");
        Periode periode = new Periode();
        Fastperiode fastperiode = new Fastperiode();
        fastperiode.setStartdato("2019-06-01");
        fastperiode.setSluttdato("2019-12-01");
        periode.setFastperiode(fastperiode);
        vedtak.setAnnenperiode(periode);
        unntak.setVedtak(vedtak);
        medlemskap.setUnntak(unntak);
        sed.setMedlemskap(medlemskap);
        Nav nav = new Nav();
        Person person = new Person();
        person.setFoedselsdato(fødselsdato.toString());
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

    OpprettJournalpostResponse journalpostResponse() {
        return OpprettJournalpostResponse.builder()
                .journalpostId("1")
                .dokumenter(Collections.singletonList(new OpprettJournalpostResponse.Dokument("1")))
                .journalstatus("OK")
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
        BUC buc = new BUC();
        buc.setId(id);
        Document document = new Document();
        document.setConversations(Arrays.asList(new Conversation(), new Conversation()));
        document.setId(id);
        buc.setDocuments(Collections.singletonList(document));
        return buc;
    }

    public PDLPerson pdlPerson(LocalDate fødselsdato, String statsborgerskapLandkode) {
        var pdlPerson = new PDLPerson();

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

        pdlPerson.setNavn(Set.of(pdlNavn));
        pdlPerson.setFoedsel(Set.of(pdlFødsel));
        pdlPerson.setStatsborgerskap(Set.of(pdlStatsborgerskap));
        pdlPerson.setFolkeregisterpersonstatus(Set.of(pdlPersonstatus));
        return pdlPerson;
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
