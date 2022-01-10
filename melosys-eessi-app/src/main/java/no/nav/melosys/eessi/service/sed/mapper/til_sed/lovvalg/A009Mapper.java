package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;
import no.nav.melosys.eessi.models.sed.nav.*;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;

import java.util.Optional;

public class A009Mapper implements LovvalgSedMapper<MedlemskapA009> {

    @Override
    public MedlemskapA009 getMedlemskap(SedDataDto sedData) {

        final MedlemskapA009 medlemskapA009 = new MedlemskapA009();
        medlemskapA009.setVedtak(getVedtak(sedData));
        medlemskapA009.setAndreland(getAndreland(sedData));
        medlemskapA009.setUtsendingsland(getUtsendingsland(sedData));

        return medlemskapA009;
    }

    private VedtakA009 getVedtak(SedDataDto sedDataDto) {
        VedtakA009 vedtak = new VedtakA009();
        final Optional<Lovvalgsperiode> lovvalgsperiode = sedDataDto.finnLovvalgsperiode();
        Periode gjelderperiode = new Periode();

        if (lovvalgsperiode.isPresent()) {
            vedtak.setLand(LandkodeMapper.mapTilLandkodeIso2(lovvalgsperiode.get().getLovvalgsland()));

            //Vil alltid være fast periode
            gjelderperiode.setFastperiode(lagFastPeriodeFraLovvalgsPeriode(lovvalgsperiode.get()));

            if (!erGyldigLovvalgbestemmelse(lovvalgsperiode.get().getBestemmelse())) {
                throw new MappingException("Lovvalgsbestemmelse er ikke av artikkel 12!");
            }
            vedtak.setArtikkelforordning(lovvalgsperiode.get().getBestemmelse().getValue());

        }

        setVedtaksdata(vedtak, sedDataDto.getVedtakDto());
        vedtak.setGjeldervarighetyrkesaktivitet("nei");
        vedtak.setGjelderperiode(gjelderperiode);
        return vedtak;
    }


    private Fastperiode lagFastPeriodeFraLovvalgsPeriode(Lovvalgsperiode lovvalgsperiode) {
        Fastperiode fastperiode = new Fastperiode();
        fastperiode.setStartdato(formaterDato(lovvalgsperiode.getFom()));
        fastperiode.setSluttdato(formaterDato(lovvalgsperiode.getTom()));
        return fastperiode;
    }

    private boolean erGyldigLovvalgbestemmelse(Bestemmelse bestemmelse) {
        return bestemmelse == Bestemmelse.ART_12_1
                || bestemmelse == Bestemmelse.ART_12_2;
    }

    private Utsendingsland getUtsendingsland(SedDataDto sedData) {
        final String lovvalgsland = sedData.finnLovvalgslandDefaultNO();
        Utsendingsland utsendingsland = new Utsendingsland();
        utsendingsland.setArbeidsgiver(hentArbeidsgivereILand(sedData.getArbeidsgivendeVirksomheter(), lovvalgsland));
        return utsendingsland;
    }

    private Utsendingsland getAndreland(SedDataDto sedData) {
        final String lovvalgsland = sedData.finnLovvalgslandDefaultNO();
        Utsendingsland utsendingsland = new Utsendingsland();
        utsendingsland.setArbeidsgiver(hentArbeidsgivereIkkeILand(sedData.getArbeidsgivendeVirksomheter(), lovvalgsland));
        return utsendingsland;
    }


    public SedType getSedType() {
        return SedType.A009;
    }
}
