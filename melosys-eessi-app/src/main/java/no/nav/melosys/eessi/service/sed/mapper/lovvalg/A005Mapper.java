package no.nav.melosys.eessi.service.sed.mapper.lovvalg;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA005;

public class A005Mapper implements LovvalgSedMapper<MedlemskapA005> {

    @Override
    public MedlemskapA005 getMedlemskap(SedDataDto sedData) {
        return new MedlemskapA005();
    }

    @Override
    public SedType getSedType() {
        return SedType.A005;
    }
}
