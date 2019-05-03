package no.nav.melosys.eessi.service.sed.mapper;

import java.time.LocalDate;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.*;


public class X001Mapper implements SedMapper {

    public SED mapFraSed(SED sed, String aarsak) {
        SED x001 = new SED();
        x001.setSed("X001");
        x001.setSedGVer(SED_G_VER);
        x001.setSedVer(SED_VER);
        x001.setNav(mapNav(sed, aarsak));

        return x001;
    }

    private Nav mapNav(SED sed, String aarsak) {
        Nav nav = new Nav();
        nav.setSak(mapSak(sed, aarsak));
        return nav;
    }

    private Sak mapSak(SED sed, String aarsak) {
        Sak sak = new Sak();
        sak.setAnmodning(mapAnmodning(aarsak));
        sak.setKontekst(mapKontekst(sed));
        return sak;
    }

    private Kontekst mapKontekst(SED sed) {
        Kontekst kontekst = new Kontekst();
        kontekst.setBruker(sed.getNav().getBruker());
        return kontekst;
    }

    private X001Anmodning mapAnmodning(String aarsakType) {
        X001Anmodning anmodning = new X001Anmodning();

        Aarsak aarsak = new Aarsak();
        aarsak.setType(aarsakType);

        Avslutning avslutning = new Avslutning();
        avslutning.setDato(LocalDate.now().format(SedMapper.dateTimeFormatter));
        avslutning.setAarsak(aarsak);
        avslutning.setType("automatisk");

        anmodning.setAvslutning(avslutning);
        return anmodning;
    }
}
