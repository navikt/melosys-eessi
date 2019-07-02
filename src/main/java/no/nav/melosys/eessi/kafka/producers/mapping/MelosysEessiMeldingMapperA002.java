package no.nav.melosys.eessi.kafka.producers.mapping;

import no.nav.melosys.eessi.kafka.producers.Periode;
import no.nav.melosys.eessi.kafka.producers.SvarAnmodningUnntak;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA002;

class MelosysEessiMeldingMapperA002 extends SvarAnmodningUnntakEessiMeldingMapper<MedlemskapA002> {

    @Override
    MedlemskapA002 hentMedlemskap(SED sed) {
        return (MedlemskapA002) sed.getMedlemskap();
    }

    @Override
    SvarAnmodningUnntak.Beslutning hentBeslutning(MedlemskapA002 medlemskap) {
        String resultat = medlemskap.getUnntak().getVedtak().getResultat();

        if ("ikke_godkjent".equals(resultat)) {
            return SvarAnmodningUnntak.Beslutning.AVSLAG;
        } else if ("godkjent_for_annen_periode".equals(resultat)) {
            return SvarAnmodningUnntak.Beslutning.DELVIS_INNVILGELSE;
        }

        throw new IllegalArgumentException("Feil ved mapping til beslutning for A002. "
                + "medlemskap.unntak.vedtak.resultat har ukjent verdi: " + resultat);
    }

    @Override
    String hentBegrunnelse(MedlemskapA002 medlemskap) {
        return medlemskap.getUnntak().getVedtak().getBegrunnelse();
    }

    @Override
    Periode hentDelvisInnvilgetPeriode(MedlemskapA002 medlemskap) {
        if ("ikke_godkjent".equals(medlemskap.getUnntak().getVedtak().getResultat())) {
            return null;
        }

        no.nav.melosys.eessi.models.sed.nav.Periode periode = medlemskap.getUnntak().getVedtak().getAnnenperiode();
        return new Periode(periode.getFastperiode().getStartdato(), periode.getFastperiode().getSluttdato());
    }
}
