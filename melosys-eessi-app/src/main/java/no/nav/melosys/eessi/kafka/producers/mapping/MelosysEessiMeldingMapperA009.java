package no.nav.melosys.eessi.kafka.producers.mapping;

import java.time.LocalDate;

import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;
import no.nav.melosys.eessi.models.sed.nav.AapenPeriode;
import no.nav.melosys.eessi.models.sed.nav.Fastperiode;
import no.nav.melosys.eessi.models.sed.nav.Periode;

class MelosysEessiMeldingMapperA009 extends NyttLovvalgEessiMeldingMapper<MedlemskapA009> {

    @Override
    Boolean sedErEndring(MedlemskapA009 medlemskap) {
        return "ja".equalsIgnoreCase(medlemskap.getVedtak().getErendringsvedtak());
    }

    @Override
    MedlemskapA009 hentMedlemskap(SED sed) {
        return (MedlemskapA009) sed.getMedlemskap();
    }

    @Override
    String hentLovvalgsbestemmelse(MedlemskapA009 medlemskap) {
        return medlemskap.getVedtak().getArtikkelforordning();
    }

    @Override
    String hentLovvalgsland(MedlemskapA009 medlemskap) {
        return medlemskap.getVedtak().getLand();
    }

    @Override
    no.nav.melosys.eessi.kafka.producers.Periode mapPeriode(MedlemskapA009 medlemskap) {

        LocalDate fom;
        LocalDate tom;

        Periode periode = medlemskap.getVedtak().getGjelderperiode();
        if (periode.erAapenPeriode()) {
            AapenPeriode aapenPeriode = periode.getAapenperiode();
            fom = aapenPeriode.getStartdato() == null ? null : LocalDate.parse(aapenPeriode.getStartdato());
            tom = null;
        } else {
            Fastperiode fastperiode = periode.getFastperiode();
            fom = fastperiode.getStartdato() == null ? null : LocalDate.parse(fastperiode.getStartdato());
            tom = fastperiode.getSluttdato() == null ? null : LocalDate.parse(fastperiode.getSluttdato());
        }

        return new no.nav.melosys.eessi.kafka.producers.Periode(fom, tom);
    }
}
