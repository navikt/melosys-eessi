package no.nav.melosys.eessi.service.sed.mapper.til_sed.administrativ;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.*;
import no.nav.melosys.eessi.service.sed.mapper.til_sed.SedMapper;

public class X008Mapper implements SedMapper {

    @Override
    public SED mapTilSed(SedDataDto sedData, Boolean erCDM4_3) {
        var sed = SedMapper.super.mapTilSed(sedData, erCDM4_3);

        Sak sakForSed = mapSak(sedData, sed);
        sed.getNav().setSak(sakForSed);

        return sed;
    }

    public Sak mapSak(SedDataDto sedData, SED sed) {
        var sak = new Sak();
        var invalideringSed = new InvalideringSed();

        invalideringSed.setType(sedData.getInvalideringSedDto().getSedTypeSomSkalInvalideres());
        invalideringSed.setUtstedelsesdato(sedData.getInvalideringSedDto().getUtstedelsedato());
        // 04  = The case was reconsidered and the grounds for the invalidated SED are no longer valid
        invalideringSed.setGrunn(new Grunn("04", ""));

        sak.setUgyldiggjoere(new Ugyldiggjoere(invalideringSed));
        sak.setKontekst(mapKontekst(sed));

        return sak;
    }

    @Override
    public SedType getSedType() {
        return SedType.X008;
    }

    private Kontekst mapKontekst(SED sed) {
        Kontekst kontekst = new Kontekst();
        kontekst.setBruker(sed.getNav().getBruker());
        return kontekst;
    }
}
