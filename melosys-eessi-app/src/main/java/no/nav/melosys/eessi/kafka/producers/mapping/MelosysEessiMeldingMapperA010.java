package no.nav.melosys.eessi.kafka.producers.mapping;

import no.nav.melosys.eessi.kafka.producers.model.Periode;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA010;

class MelosysEessiMeldingMapperA010 extends NyttLovvalgEessiMeldingMapper<MedlemskapA010> {

    @Override
    Periode mapPeriode(MedlemskapA010 medlemskap) {
        return hentPeriode(medlemskap.getVedtak().getGjelderperiode());
    }

    @Override
    String hentLovvalgsland(MedlemskapA010 medlemskap) {
        return medlemskap.getVedtak().getLand();
    }

    @Override
    String hentLovvalgsbestemmelse(MedlemskapA010 medlemskap) {
        return medlemskap.getMeldingomlovvalg().getArtikkel();
    }

    @Override
    Boolean sedErEndring(MedlemskapA010 medlemskap) {
        return "nei".equalsIgnoreCase(medlemskap.getVedtak().getEropprinneligvedtak());
    }

    MedlemskapA010 hentMedlemskap(SED sed) {
        return (MedlemskapA010) sed.getMedlemskap();
    }
}
