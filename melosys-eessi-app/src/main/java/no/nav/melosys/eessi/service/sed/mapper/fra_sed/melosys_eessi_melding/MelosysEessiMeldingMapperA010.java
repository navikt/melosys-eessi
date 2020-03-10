package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding;

import no.nav.melosys.eessi.kafka.producers.model.Periode;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA010;

class MelosysEessiMeldingMapperA010 implements NyttLovvalgEessiMeldingMapper<MedlemskapA010> {

    @Override
    public Periode mapPeriode(MedlemskapA010 medlemskap) {
        var periode = hentPeriode(medlemskap.getVedtak().getGjelderperiode());
        return new Periode(periode.getFom(), periode.getTom());
    }

    @Override
    public String hentLovvalgsland(MedlemskapA010 medlemskap) {
        return medlemskap.getVedtak().getLand();
    }

    @Override
    public String hentLovvalgsbestemmelse(MedlemskapA010 medlemskap) {
        return medlemskap.getMeldingomlovvalg().getArtikkel();
    }

    @Override
    public Boolean sedErEndring(MedlemskapA010 medlemskap) {
        return "nei".equalsIgnoreCase(medlemskap.getVedtak().getEropprinneligvedtak());
    }

    public MedlemskapA010 hentMedlemskap(SED sed) {
        return (MedlemskapA010) sed.getMedlemskap();
    }
}
