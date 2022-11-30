package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.kafka.producers.model.Periode;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA010;

@Slf4j
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
        var erEndring = "nei".equalsIgnoreCase(medlemskap.getVedtak().getEropprinneligvedtak());
        log.info("sedErEndring i A010 er {}, med erendringsvedtak: {} og eropprinneligvedtak: {}", erEndring,
            medlemskap.getVedtak().getErendringsvedtak(), medlemskap.getVedtak().getEropprinneligvedtak());
        return erEndring;
    }

    public MedlemskapA010 hentMedlemskap(SED sed) {
        return (MedlemskapA010) sed.getMedlemskap();
    }
}
