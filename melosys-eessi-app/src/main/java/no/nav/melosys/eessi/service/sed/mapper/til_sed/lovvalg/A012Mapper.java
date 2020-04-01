package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA012;

public class A012Mapper implements LovvalgSedMapper<MedlemskapA012> {

    @Override
    public MedlemskapA012 getMedlemskap(SedDataDto sedData) {
        return new MedlemskapA012();
    }

    @Override
    public SedType getSedType() {
        return SedType.A012;
    }
}
