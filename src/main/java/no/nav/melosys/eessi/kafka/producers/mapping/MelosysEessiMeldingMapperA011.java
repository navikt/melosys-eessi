package no.nav.melosys.eessi.kafka.producers.mapping;

import no.nav.melosys.eessi.kafka.producers.Periode;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA011;

public class MelosysEessiMeldingMapperA011 extends MelosysEessiMeldingMapper<MedlemskapA011> {

    @Override
    Periode mapPeriode(MedlemskapA011 medlemskap) {
        return null;
    }

    @Override
    String hentLovvalgsland(MedlemskapA011 medlemskap) {
        return null;
    }

    @Override
    String hentLovvalgsbestemmelse(MedlemskapA011 medlemskap) {
        return null;
    }

    @Override
    Boolean sedErEndring(MedlemskapA011 medlemskap) {
        return false;
    }

    @Override
    MedlemskapA011 hentMedlemskap(SED sed) {
        return null;
    }
}
