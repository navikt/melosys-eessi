package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding;

import no.nav.melosys.eessi.kafka.producers.model.AnmodningUnntak;
import no.nav.melosys.eessi.kafka.producers.model.Periode;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA001;

import java.util.Optional;

import static no.nav.melosys.eessi.models.DatoUtils.tilLocalDate;

class MelosysEessiMeldingMapperA001 implements NyttLovvalgEessiMeldingMapper<MedlemskapA001> {
    private static final String ARTIKKEL_16_1 = "16_1";

    @Override
    public Periode mapPeriode(MedlemskapA001 medlemskap) {
        return new Periode(
                tilLocalDate(medlemskap.getSoeknadsperiode().getStartdato()),
                tilLocalDate(medlemskap.getSoeknadsperiode().getSluttdato())
        );
    }

    @Override
    public String hentLovvalgsland(MedlemskapA001 medlemskap) {
        return medlemskap.getForespurtmedlemskap().iterator().next().getLandkode();
    }

    @Override
    public String hentLovvalgsbestemmelse(MedlemskapA001 medlemskap) {
        return ARTIKKEL_16_1; // Denne er alltid 16.1
    }

    @Override
    public AnmodningUnntak hentAnmodningUnntak(MedlemskapA001 medlemskap) {
        AnmodningUnntak anmodningUnntak = new AnmodningUnntak();
        anmodningUnntak.setUnntakFraLovvalgsland(hentUnntakFraLovvalgsland(medlemskap));
        anmodningUnntak.setUnntakFraLovvalgsbestemmelse(hentUnntakFraLovvalgsbestemmelse(medlemskap));
        return anmodningUnntak;
    }

    public String hentUnntakFraLovvalgsland(MedlemskapA001 medlemskap) {
        return medlemskap.getNaavaerendemedlemskap().iterator().next().getLandkode();
    }

    public String hentUnntakFraLovvalgsbestemmelse(MedlemskapA001 medlemskap) {
        return medlemskap.getUnntak().getGrunnlag().getArtikkel();
    }

    @Override
    public Boolean sedErEndring(MedlemskapA001 medlemskap) {
        if (medlemskap.getAnmodning() == null) {
            return false;
        }

        return "ja".equalsIgnoreCase(medlemskap.getAnmodning().getErendring());
    }

    @Override
    public MedlemskapA001 hentMedlemskap(SED sed) {
        return (MedlemskapA001) sed.getMedlemskap();
    }

    @Override
    public Optional<Boolean> mapErOpprinneligVedtak(MedlemskapA001 medlemskap) {
        return Optional.empty();
    }
}
