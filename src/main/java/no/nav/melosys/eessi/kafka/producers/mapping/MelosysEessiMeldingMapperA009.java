package no.nav.melosys.eessi.kafka.producers.mapping;

import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;
import no.nav.melosys.eessi.models.sed.nav.AapenPeriode;
import no.nav.melosys.eessi.models.sed.nav.Fastperiode;
import no.nav.melosys.eessi.models.sed.nav.Periode;

class MelosysEessiMeldingMapperA009 extends MelosysEessiMeldingMapper {

    @Override
    Boolean sedErEndring(SED sed) {

        MedlemskapA009 medlemskapA009 = hentMedlemskap(sed);
        return "ja".equalsIgnoreCase(medlemskapA009.getVedtak().getErendringsvedtak());
    }

    @Override
    String hentLovvalgsbestemmelse(SED sed) {
        MedlemskapA009 medlemskapA009 = hentMedlemskap(sed);
        return medlemskapA009.getVedtak().getArtikkelforordning();
    }

    @Override
    String hentLovvalgsland(SED sed) {
        MedlemskapA009 medlemskapA009 = hentMedlemskap(sed);
        return medlemskapA009.getVedtak().getLand();
    }

    @Override
    no.nav.melosys.eessi.kafka.producers.Periode mapPeriode(SED sed) {

        String fom;
        String tom;

        MedlemskapA009 medlemskapA009 = hentMedlemskap(sed);
        Periode periode = medlemskapA009.getVedtak().getGjelderperiode();
        if (periode.erAapenPeriode()) {
            AapenPeriode aapenPeriode = periode.getAapenperiode();
            fom = aapenPeriode.getStartdato();
            tom = null;
        } else {
            Fastperiode fastperiode = periode.getFastperiode();
            fom = fastperiode.getStartdato();
            tom = fastperiode.getSluttdato();
        }

        return new no.nav.melosys.eessi.kafka.producers.Periode(fom, tom);
    }

    private MedlemskapA009 hentMedlemskap(SED sed) {
        return (MedlemskapA009) sed.getMedlemskap();
    }
}
