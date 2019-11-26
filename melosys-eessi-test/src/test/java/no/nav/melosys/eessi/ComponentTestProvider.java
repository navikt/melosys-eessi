package no.nav.melosys.eessi;

import no.nav.melosys.eessi.integration.dokkat.dto.DokumentTypeInfoDto;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Conversation;
import no.nav.melosys.eessi.models.buc.Document;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA002;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.UnntakA002;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.VedtakA002;
import no.nav.melosys.eessi.models.sed.nav.Bruker;
import no.nav.melosys.eessi.models.sed.nav.Periode;
import no.nav.melosys.eessi.models.sed.nav.Person;
import no.nav.melosys.eessi.models.sed.nav.Statsborgerskap;
import no.nav.melosys.eessi.models.sed.nav.*;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;

import static no.nav.melosys.eessi.models.BucType.LA_BUC_01;
import static no.nav.melosys.eessi.models.SedType.A002;

public class ComponentTestProvider {
    public ComponentTestProvider() {
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

    SedHendelse sedHendelse(String aktoerId) {
        return SedHendelse.builder()
                .avsenderId("avsenderId")
                .avsenderNavn("avsender")
                .bucType(LA_BUC_01.name())
                .id(42L)
                .mottakerId("mottakerId")
                .mottakerNavn("mottakerNavn")
                .navBruker(aktoerId)
                .rinaDokumentId("1")
                .rinaSakId("1")
                .rinaDokumentVersjon("1")
                .sedType(A002.name())
                .sedId("sedId")
                .sektorKode("LA")
                .build();
    }

    SED sed(LocalDate fødselsdato, String statsborgerskap) {
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
        person.setStatsborgerskap(Arrays.asList(statsborgerskap1));
        Bruker bruker = new Bruker();
        bruker.setPerson(person);
        nav.setBruker(bruker);
        sed.setNav(nav);
        return sed;
    }

    HentPersonResponse hentPersonResponse(String aktoerId1, LocalDate fødselsdato, String landkode) throws DatatypeConfigurationException {
        HentPersonResponse hentPersonResponse = new HentPersonResponse();
        no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person = new no.nav.tjeneste.virksomhet.person.v3.informasjon.Person();
        AktoerId aktoerIdObject = new AktoerId().withAktoerId(aktoerId1);
        person.setAktoer(aktoerIdObject);
        person.setPersonstatus(new Personstatus().withPersonstatus(new Personstatuser().withValue("ADNR")));
        Foedselsdato foedselsdato = new Foedselsdato();
        foedselsdato.setFoedselsdato(DatatypeFactory.newInstance().newXMLGregorianCalendar(fødselsdato.toString()));
        person.setFoedselsdato(foedselsdato);
        no.nav.tjeneste.virksomhet.person.v3.informasjon.Statsborgerskap statsborgerskap = new no.nav.tjeneste.virksomhet.person.v3.informasjon.Statsborgerskap().withLand(new Landkoder().withValue(landkode));
        person.setStatsborgerskap(statsborgerskap);
        hentPersonResponse.setPerson(person);
        return hentPersonResponse;
    }

    OpprettJournalpostResponse journalpostResponse() {
        return OpprettJournalpostResponse.builder()
                .journalpostId("1")
                .dokumenter(Arrays.asList(new OpprettJournalpostResponse.Dokument("1")))
                .journalstatus("OK")
                .melding("OK")
                .build();
    }

    Sak sak(String aktoerId) {
        return new Sak("1", "GEN", "MELOSYS", aktoerId, "012356745", "1", "Tester", ZonedDateTime.now());
    }

    SedMedVedlegg sedMedVedlegg() {
        return new SedMedVedlegg(new SedMedVedlegg.BinaerFil("fil123","", new byte[0]), Collections.emptyList());
    }

    BUC buc(String id) {
        BUC buc = new BUC();
        buc.setId(id);
        Document document = new Document();
        document.setConversations(Arrays.asList(new Conversation(), new Conversation()));
        document.setId(id);
        buc.setDocuments(Arrays.asList(document));
        return buc;
    }
}
