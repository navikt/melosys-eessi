package no.nav.melosys.eessi;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import no.nav.dokkat.api.tkat020.v4.DokumentTypeInfoToV4;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Conversation;
import no.nav.melosys.eessi.models.buc.Document;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA002;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.UnntakA002;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.VedtakA002;
import no.nav.melosys.eessi.models.sed.nav.*;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.AktoerId;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Foedselsdato;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Landkoder;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;

import static no.nav.melosys.eessi.models.BucType.LA_BUC_01;
import static no.nav.melosys.eessi.models.SedType.A002;

public class ComponentTestProvider {
    public ComponentTestProvider() {
    }

    DokumentTypeInfoToV4 dokumentTypeInfoToV4() {
        DokumentTypeInfoToV4 dokumentTypeInfoToV4 = new DokumentTypeInfoToV4();
        dokumentTypeInfoToV4.setDokumentKategori("dokumentKategori");
        dokumentTypeInfoToV4.setDokumentTittel("dokumentTittel");
        dokumentTypeInfoToV4.setDokumenttypeId("dokumenttypeId");
        dokumentTypeInfoToV4.setBehandlingstema("behandlingstema");
        dokumentTypeInfoToV4.setTema("tema");
        return dokumentTypeInfoToV4;
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
        sed.setSed(A002.name());

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