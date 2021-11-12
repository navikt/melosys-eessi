package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag;

import no.nav.melosys.eessi.controller.dto.SedGrunnlagDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.SED;

public class SedGrunnlagMapperA001 implements SedGrunnlagMapper{

    @Override
    public SedGrunnlagDto map(SED sed) {
        SedGrunnlagDto sedGrunnlagDto = SedGrunnlagMapper.super.map(sed);
        sedGrunnlagDto.setSedType(SedType.A001.name());
        return sedGrunnlagDto;
    }
}
