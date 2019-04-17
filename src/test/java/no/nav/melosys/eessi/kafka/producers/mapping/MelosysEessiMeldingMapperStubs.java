package no.nav.melosys.eessi.kafka.producers.mapping;

import java.util.Collections;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.models.sed.nav.Bruker;
import no.nav.melosys.eessi.models.sed.nav.Nav;
import no.nav.melosys.eessi.models.sed.nav.Person;
import no.nav.melosys.eessi.models.sed.nav.Statsborgerskap;
import no.nav.melosys.eessi.service.joark.SakInformasjon;

class MelosysEessiMeldingMapperStubs {

    static SED createSed(Medlemskap medlemskap) {
        SED sed = new SED();
        sed.setMedlemskap(medlemskap);
        sed.setNav(new Nav());
        sed.getNav().setBruker(new Bruker());
        sed.getNav().getBruker().setPerson(new Person());

        Statsborgerskap statsborgerskap = new Statsborgerskap();
        statsborgerskap.setLand("SE");
        sed.getNav().getBruker().getPerson().setStatsborgerskap(Collections.singletonList(statsborgerskap));

        return sed;
    }

    static SedHendelse creteSedHendelse() {
        return SedHendelse.builder().navBruker("navbruker").rinaDokumentId("rinadok").rinaSakId("rinasak").avsenderId("avsenderid").avsenderNavn("avsendernavn")
                .bucType("buc").sedType("sed").id(1L).build();
    }

    static SakInformasjon createSakInformasjon() {
        return SakInformasjon.builder().gsakSaksnummer("123").journalpostId("journalpost").dokumentId("dokument").build();
    }
}
