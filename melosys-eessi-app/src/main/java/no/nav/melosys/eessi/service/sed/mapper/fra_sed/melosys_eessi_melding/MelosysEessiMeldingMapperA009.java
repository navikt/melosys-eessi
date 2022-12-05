package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding;

import java.time.LocalDate;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;
import no.nav.melosys.eessi.models.sed.nav.AapenPeriode;
import no.nav.melosys.eessi.models.sed.nav.Fastperiode;
import no.nav.melosys.eessi.models.sed.nav.Periode;

import static no.nav.melosys.eessi.models.DatoUtils.tilLocalDate;

@Slf4j
class MelosysEessiMeldingMapperA009 implements NyttLovvalgEessiMeldingMapper<MedlemskapA009> {

    @Override
    public Boolean sedErEndring(MedlemskapA009 medlemskap) {
        var erEndring = !"ja".equalsIgnoreCase(medlemskap.getVedtak().getEropprinneligvedtak());
        log.info("sedErEndring i A009 er {}, med erendringsvedtak: {} og eropprinneligvedtak: {}", erEndring,
            medlemskap.getVedtak().getErendringsvedtak(), medlemskap.getVedtak().getEropprinneligvedtak());
        return erEndring;
    }

    @Override
    public MedlemskapA009 hentMedlemskap(SED sed) {
        return (MedlemskapA009) sed.getMedlemskap();
    }

    @Override
    public String hentLovvalgsbestemmelse(MedlemskapA009 medlemskap) {
        return medlemskap.getVedtak().getArtikkelforordning();
    }

    @Override
    public String hentLovvalgsland(MedlemskapA009 medlemskap) {
        return medlemskap.getVedtak().getLand();
    }

    @Override
    public no.nav.melosys.eessi.kafka.producers.model.Periode mapPeriode(MedlemskapA009 medlemskap) {

        LocalDate fom;
        LocalDate tom;

        Periode periode = medlemskap.getVedtak().getGjelderperiode();
        if (periode.erAapenPeriode()) {
            AapenPeriode aapenPeriode = periode.getAapenperiode();
            fom = tilLocalDate(aapenPeriode.getStartdato());
            tom = null;
        } else {
            Fastperiode fastperiode = periode.getFastperiode();
            fom = tilLocalDate(fastperiode.getStartdato());
            tom = tilLocalDate(fastperiode.getSluttdato());
        }

        return new no.nav.melosys.eessi.kafka.producers.model.Periode(fom, tom);
    }
}
