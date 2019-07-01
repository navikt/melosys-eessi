package no.nav.melosys.eessi.kafka.producers.mapping;

import no.nav.melosys.eessi.kafka.producers.MelosysEessiMelding;
import no.nav.melosys.eessi.kafka.producers.Periode;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA011;

public class MelosysEessiMeldingMapperA011 extends SvarAnmodningUnntakEessiMeldingMapper<MedlemskapA011> {

    @Override
    MedlemskapA011 hentMedlemskap(SED sed) {
        return (MedlemskapA011) sed.getMedlemskap();
    }

    @Override
    MelosysEessiMelding.Beslutning hentBeslutning(MedlemskapA011 medlemskap) {
        return MelosysEessiMelding.Beslutning.INNVILGELSE;
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
