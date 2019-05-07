package no.nav.melosys.eessi.kafka.producers.mapping;

import no.nav.melosys.eessi.kafka.producers.Periode;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA010;
import no.nav.melosys.eessi.models.sed.nav.PeriodeA010;

class MelosysEessiMeldingMapperA010 extends MelosysEessiMeldingMapper {

    @Override
    Periode mapPeriode(SED sed) {
        PeriodeA010 periode = hentMedlemskap(sed).getVedtak().getGjelderperiode();

        String fom;
        String tom;

        if (periode.erAapenPeriode()) {
            fom = periode.getAapenperiode().getStartdato();
            tom = null;
        } else {
            fom = periode.getStartdato();
            tom = periode.getSluttdato();
        }
        return new Periode(fom, tom);
    }

    @Override
    String hentLovvalgsland(SED sed) {
        return hentMedlemskap(sed).getVedtak().getLand();
    }

    @Override
    String hentLovvalgsbestemmelse(SED sed) {
        return hentMedlemskap(sed).getMeldingomlovvalg().getArtikkel();
    }

    @Override
    Boolean sedErEndring(SED sed) {
        return "nei".equalsIgnoreCase(hentMedlemskap(sed).getVedtak().getEropprinneligvedtak());
    }

    MedlemskapA010 hentMedlemskap(SED sed) {
        return (MedlemskapA010) sed.getMedlemskap();
    }
}
