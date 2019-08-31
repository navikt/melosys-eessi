package no.nav.melosys.eessi.kafka.producers.mapping;

import no.nav.melosys.eessi.kafka.producers.model.Periode;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003;

class MelosysEessiMeldingMapperA003 extends NyttLovvalgEessiMeldingMapper<MedlemskapA003> {

    @Override
    Periode mapPeriode(MedlemskapA003 medlemskap) {
        return hentPeriode(medlemskap.getVedtak().getGjelderperiode());
    }

    @Override
    String hentLovvalgsland(MedlemskapA003 medlemskap) {
        return medlemskap.getVedtak().getLand();
    }

    @Override
    String hentLovvalgsbestemmelse(MedlemskapA003 medlemskap) {
        return medlemskap.getRelevantartikkelfor8832004eller9872009();
    }

    @Override
    Boolean sedErEndring(MedlemskapA003 medlemskap) {
        return "ja".equals(medlemskap.getVedtak().getErendringsvedtak());
    }

    @Override
    MedlemskapA003 hentMedlemskap(SED sed) {
        return (MedlemskapA003) sed.getMedlemskap();
    }

    @Override
    boolean erMidlertidigBestemmelse(MedlemskapA003 medlemskap) {
        return "ja".equals(medlemskap.getIsDeterminationProvisional());
    }
}
