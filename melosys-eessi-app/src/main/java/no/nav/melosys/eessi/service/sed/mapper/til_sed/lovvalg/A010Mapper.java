package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA010;
import no.nav.melosys.eessi.models.sed.nav.MeldingOmLovvalg;
import no.nav.melosys.eessi.models.sed.nav.PeriodeA010;
import no.nav.melosys.eessi.models.sed.nav.Utsendingsland;
import no.nav.melosys.eessi.models.sed.nav.VedtakA010;

public class A010Mapper implements LovvalgSedMapper<MedlemskapA010> {

    @Override
    public MedlemskapA010 getMedlemskap(SedDataDto sedData) throws MappingException, NotFoundException {
        MedlemskapA010 medlemskap = new MedlemskapA010();

        Lovvalgsperiode lovvalgsperiode = getLovvalgsperiode(sedData);

        if (lovvalgsperiode != null) {
            medlemskap.setMeldingomlovvalg(hentMeldingOmLovvalg(lovvalgsperiode));
            medlemskap.setVedtak(hentVedtak(lovvalgsperiode));
        }

        medlemskap.setAndreland(getAndreland(sedData));
        return medlemskap;
    }

    private Utsendingsland getAndreland(SedDataDto sedData) throws MappingException, NotFoundException {
        if (sedData.getUtenlandskeVirksomheter() == null) {
            return null; //Ikke påkrevd
        }

        Utsendingsland utsendingsland = new Utsendingsland();
        utsendingsland.setArbeidsgiver(hentArbeidsGiver(sedData.getUtenlandskeVirksomheter()));
        return utsendingsland;
    }

    private VedtakA010 hentVedtak(Lovvalgsperiode lovvalgsperiode) {
        VedtakA010 vedtak = new VedtakA010();
        vedtak.setEropprinneligvedtak("ja");
        vedtak.setGjelderperiode(hentPeriode(lovvalgsperiode));
        vedtak.setLand(lovvalgsperiode.getLovvalgsland());
        vedtak.setGjeldervarighetyrkesaktivitet("ja");
        return vedtak;
    }

    private MeldingOmLovvalg hentMeldingOmLovvalg(Lovvalgsperiode lovvalgsperiode) {
        MeldingOmLovvalg meldingOmLovvalg = new MeldingOmLovvalg();
        meldingOmLovvalg.setArtikkel(lovvalgsperiode.getBestemmelse().getValue());
        return  meldingOmLovvalg;
    }

    private PeriodeA010 hentPeriode(Lovvalgsperiode lovvalgsperiode) {
        PeriodeA010 periode = new PeriodeA010();
        periode.setStartdato(formaterDato(lovvalgsperiode.getFom()));
        periode.setSluttdato(formaterDato(lovvalgsperiode.getTom()));
        return periode;
    }

    @Override
    public SedType getSedType() {
        return SedType.A010;
    }
}
