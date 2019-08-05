package no.nav.melosys.eessi.service.sed.mapper;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA008;

public class A008Mapper implements LovvalgSedMapper<MedlemskapA008> {

    @Override
    public MedlemskapA008 getMedlemskap(SedDataDto sedData) {
        return new MedlemskapA008();
    }

    @Override
    public SedType getSedType() {
        return SedType.A008;
    }
}
