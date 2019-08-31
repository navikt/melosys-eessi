package no.nav.melosys.eessi.kafka.producers.mapping;

import no.nav.melosys.eessi.kafka.producers.model.Periode;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA011;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.SvarAnmodningUnntakBeslutning;

class MelosysEessiMeldingMapperA011 extends SvarAnmodningUnntakEessiMeldingMapper<MedlemskapA011> {

    @Override
    MedlemskapA011 hentMedlemskap(SED sed) {
        return (MedlemskapA011) sed.getMedlemskap();
    }

    @Override
    SvarAnmodningUnntakBeslutning hentBeslutning(MedlemskapA011 medlemskap) {
        return SvarAnmodningUnntakBeslutning.INNVILGELSE;
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
