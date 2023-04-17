package no.nav.melosys.eessi.service.sed.mapper.til_sed.administrativ;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Grunn;
import no.nav.melosys.eessi.models.sed.nav.InvalideringSed;
import no.nav.melosys.eessi.models.sed.nav.Sak;
import no.nav.melosys.eessi.models.sed.nav.Ugyldiggjoere;
import no.nav.melosys.eessi.service.sed.mapper.til_sed.SedMapper;

public class X008Mapper implements SedMapper {

    @Override
    public SED mapTilSed(SedDataDto sedData) {
        var sed = SedMapper.super.mapTilSed(sedData);

        sed.getNav().setSak(mapSak(sedData));

        return sed;
    }

    public Sak mapSak(SedDataDto sedData) {
        var sak = new Sak();
        var invalideringSed = new InvalideringSed();

        invalideringSed.setType(sedData.getInvalideringSedDto().getSedTypeSomSkalInvalideres());
        invalideringSed.setUtstedelsesdato(sedData.getInvalideringSedDto().getUtstedelsedato());
        invalideringSed.setGrunn(new Grunn("04", ""));

        sak.setUgyldiggjoere(new Ugyldiggjoere(invalideringSed));

        return sak;
    }

    @Override
    public SedType getSedType() {
        return SedType.X008;
    }
}
