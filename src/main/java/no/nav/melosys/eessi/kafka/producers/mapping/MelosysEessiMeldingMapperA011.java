package no.nav.melosys.eessi.kafka.producers.mapping;

import no.nav.melosys.eessi.kafka.producers.Periode;
import no.nav.melosys.eessi.kafka.producers.SvarAnmodningUnntak;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA011;

public class MelosysEessiMeldingMapperA011 extends SvarAnmodningUnntakEessiMeldingMapper<MedlemskapA011> {

    @Override
    MedlemskapA011 hentMedlemskap(SED sed) {
        return (MedlemskapA011) sed.getMedlemskap();
    }

    @Override
    SvarAnmodningUnntak.Beslutning hentBeslutning(MedlemskapA011 medlemskap) {
        return SvarAnmodningUnntak.Beslutning.INNVILGELSE;
    }

    @Override
    String hentBegrunnelse(MedlemskapA011 medlemskap) {
        return null;
    }

    @Override
    Periode hentDelvisInnvilgetPeriode(MedlemskapA011 medlemskap) {
        return null;
    }
}
