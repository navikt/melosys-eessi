package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.controller.dto.VedtakDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;
import no.nav.melosys.eessi.models.sed.nav.Fastperiode;
import no.nav.melosys.eessi.models.sed.nav.Periode;
import no.nav.melosys.eessi.models.sed.nav.Utsendingsland;
import no.nav.melosys.eessi.models.sed.nav.VedtakA009;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;

public class A009Mapper implements LovvalgSedMapper<MedlemskapA009> {

    @Override
    public MedlemskapA009 getMedlemskap(SedDataDto sedData) {

        final MedlemskapA009 medlemskapA009 = new MedlemskapA009();
        final Lovvalgsperiode lovvalgsperiode = getLovvalgsperiode(sedData);
        final VedtakDto vedtakDto = sedData.getVedtakDto();

        if (!sedData.getLovvalgsperioder().isEmpty()) {
            medlemskapA009.setVedtak(getVedtak(lovvalgsperiode,vedtakDto));
        }

        medlemskapA009.setAndreland(getAndreland(sedData));
        medlemskapA009.setUtsendingsland(getUtsendingsland(sedData));

        return medlemskapA009;
    }

    private VedtakA009 getVedtak(Lovvalgsperiode lovvalgsperiode,VedtakDto vedtakDto) {
        VedtakA009 vedtak = new VedtakA009();
        setVedtakDto(vedtakDto,vedtak);
        //vedtak.setEropprinneligvedtak(
        //        "ja"); //Confluence: "I første omgang støttes kun IntionDecision = Ja". Setter derfor ikke datoforrigevedtak eller erendringsvedtak
        vedtak.setLand(LandkodeMapper.getLandkodeIso2(lovvalgsperiode.getLovvalgsland()));
        vedtak.setGjeldervarighetyrkesaktivitet(
                "nei"); //Vil være 'ja' om det er åpen periode. Melosys støtter ikke åpen periode.

        if (!erGyldigLovvalgbestemmelse(lovvalgsperiode.getBestemmelse())) {
            throw new MappingException("Lovvalgsbestemmelse is not of article 12!");
        }

        vedtak.setArtikkelforordning(lovvalgsperiode.getBestemmelse().getValue());

        Periode gjelderperiode = new Periode();

        //Vil alltid være fast periode
        Fastperiode fastperiode = new Fastperiode();
        fastperiode.setStartdato(formaterDato(lovvalgsperiode.getFom()));
        fastperiode.setSluttdato(formaterDato(lovvalgsperiode.getTom()));
        gjelderperiode.setFastperiode(fastperiode);

        vedtak.setGjelderperiode(gjelderperiode);

        return vedtak;
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

    private void setVedtakDto(VedtakDto vedtakDto, VedtakA009 vedtakA009)
    {

        if(vedtakDto != null) {
            if (!vedtakDto.isErFoerstegangsVedtak()) {
                vedtakA009.setEropprinneligvedtak("nei");
                vedtakA009.setDatoforrigevedtak(vedtakDto.getDatoForrigePeriode().toString());
            }
        }
        else{
            vedtakA009.setEropprinneligvedtak("ja");
            vedtakA009.setDatoforrigevedtak(null);
        }
    }

    public SedType getSedType() {
        return SedType.A009;
    }
}
