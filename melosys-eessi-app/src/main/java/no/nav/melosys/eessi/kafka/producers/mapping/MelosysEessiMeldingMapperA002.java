package no.nav.melosys.eessi.kafka.producers.mapping;

import java.time.LocalDate;
import no.nav.melosys.eessi.kafka.producers.model.Periode;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA002;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.SvarAnmodningUnntakBeslutning;

class MelosysEessiMeldingMapperA002 extends SvarAnmodningUnntakEessiMeldingMapper<MedlemskapA002> {

    @Override
    MedlemskapA002 hentMedlemskap(SED sed) {
        return (MedlemskapA002) sed.getMedlemskap();
    }

    @Override
    SvarAnmodningUnntakBeslutning hentBeslutning(MedlemskapA002 medlemskap) {
        String resultat = medlemskap.getUnntak().getVedtak().getResultat();

        return SvarAnmodningUnntakBeslutning.fraRinaKode(resultat);
    }

    @Override
    String hentBegrunnelse(MedlemskapA002 medlemskap) {
        return medlemskap.getUnntak().getVedtak().getBegrunnelse();
    }

    @Override
    Periode hentDelvisInnvilgetPeriode(MedlemskapA002 medlemskap) {
        if (SvarAnmodningUnntakBeslutning.fraRinaKode(medlemskap.getUnntak().getVedtak().getResultat()) == SvarAnmodningUnntakBeslutning.AVSLAG) {
            return null;
        }

        no.nav.melosys.eessi.models.sed.nav.Periode periode = medlemskap.getUnntak().getVedtak().getAnnenperiode();
        return new Periode(LocalDate.parse(periode.getFastperiode().getStartdato()), LocalDate.parse(periode.getFastperiode().getSluttdato()));
    }
}
