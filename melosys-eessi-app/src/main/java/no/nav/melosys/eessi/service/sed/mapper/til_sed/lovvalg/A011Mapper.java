package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA011;

import static no.nav.melosys.eessi.models.sed.Constants.SED_G_VER;
import static no.nav.melosys.eessi.models.sed.Constants.SED_VER;

public class A011Mapper implements LovvalgSedMapper<MedlemskapA011> {

    public SED mapFraSed(SED sed) {
        SED a011 = new SED();
        a011.setSedType(SedType.A011.toString());
        a011.setSedGVer(SED_G_VER);
        a011.setSedVer(SED_VER);
        a011.setNav(sed.getNav());
        a011.setMedlemskap(new MedlemskapA011());

        return a011;
    }

    @Override
    public MedlemskapA011 getMedlemskap(SedDataDto sedData) {
        return new MedlemskapA011();
    }

    @Override
    public SedType getSedType() {
        return SedType.A011;
    }
}
