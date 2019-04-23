package no.nav.melosys.eessi.service.sed.mapper;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SedType;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;
import no.nav.melosys.eessi.models.sed.nav.Fastperiode;
import no.nav.melosys.eessi.models.sed.nav.Periode;
import no.nav.melosys.eessi.models.sed.nav.Utsendingsland;
import no.nav.melosys.eessi.models.sed.nav.VedtakA009;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;

public class A009Mapper implements SedMapper<MedlemskapA009> {

    @Override
    public MedlemskapA009 getMedlemskap(SedDataDto sedData) throws MappingException, NotFoundException {

        final MedlemskapA009 medlemskapA009 = new MedlemskapA009();
        final Lovvalgsperiode lovvalgsperiode = getLovvalgsperiode(sedData);

        medlemskapA009.setVedtak(getVedtak(lovvalgsperiode));
        medlemskapA009.setAndreland(getAndreland(sedData));
        medlemskapA009.setUtsendingsland(getUtsendingsland(sedData));

        return medlemskapA009;
    }

    private VedtakA009 getVedtak(Lovvalgsperiode lovvalgsperiode) throws MappingException, NotFoundException {
        VedtakA009 vedtak = new VedtakA009();

        vedtak.setEropprinneligvedtak(
                "ja"); //Confluence: "I første omgang støttes kun IntionDecision = Ja". Setter derfor ikke datoforrigevedtak eller erendringsvedtak
        vedtak.setLand(LandkodeMapper.getLandkodeIso2(lovvalgsperiode.getLovvalgsland()));
        vedtak.setGjeldervarighetyrkesaktivitet(
                "nei"); //Vil være 'ja' om det er åpen periode. Melosys støtter ikke åpen periode.

        if (!isKorrektLovvalgbestemmelse(lovvalgsperiode.getBestemmelse())) {
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

    private boolean isKorrektLovvalgbestemmelse(Bestemmelse bestemmelse) {
        return bestemmelse == Bestemmelse.ART_12_1
                || bestemmelse == Bestemmelse.ART_12_2;
    }

    private Utsendingsland getUtsendingsland(SedDataDto sedData) throws MappingException, NotFoundException {
        Utsendingsland utsendingsland = new Utsendingsland();
        utsendingsland.setArbeidsgiver(hentArbeidsGiver(sedData.getArbeidsgivendeVirksomheter()));
        return utsendingsland;
    }

    private Utsendingsland getAndreland(SedDataDto sedData) throws MappingException, NotFoundException {
        if (sedData.getUtenlandskeVirksomheter() == null) {
            return null; //Ikke påkrevd
        }

        Utsendingsland utsendingsland = new Utsendingsland();
        utsendingsland.setArbeidsgiver(hentArbeidsGiver(sedData.getUtenlandskeVirksomheter()));
        return utsendingsland;
    }

    public SedType getSedType() {
        return SedType.A009;
    }
}
