package no.nav.melosys.eessi.service.sed.mapper.til_sed.administrativ;

import java.time.LocalDate;

import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.Konstanter;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.*;

import static no.nav.melosys.eessi.models.sed.Konstanter.*;


public class X001Mapper implements AdministrativSedMapper {

    public SED mapFraSed(SED sed, String aarsak, Boolean erCDM4_3) {
        SED x001 = new SED();
        x001.setSedType(SedType.X001.toString());
        x001.setSedGVer(DEFAULT_SED_G_VER);
        x001.setSedVer(erCDM4_3 ? SED_VER_CDM_4_3 : DEFAULT_SED_VER);
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
        avslutning.setDato(LocalDate.now().format(Konstanter.dateTimeFormatter));
        avslutning.setAarsak(aarsak);
        avslutning.setType("automatisk");

        anmodning.setAvslutning(avslutning);
        return anmodning;
    }

    @Override
    public SedType getSedType() {
        return SedType.X001;
    }
}
