package no.nav.melosys.eessi.kafka.producers.mapping;

import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiMelding;
import no.nav.melosys.eessi.kafka.producers.Periode;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.service.joark.SakInformasjon;

public abstract class SvarAnmodningUnntakEessiMeldingMapper<T extends Medlemskap> implements MelosysEessiMeldingMapper {

    @Override
    public MelosysEessiMelding map(String aktoerId, SED sed, SedHendelse sedHendelse, SakInformasjon sakInformasjon,
            boolean sedErEndring) {
        MelosysEessiMelding melosysEessiMelding = MelosysEessiMeldingMapper.super.map(aktoerId, sed, sedHendelse, sakInformasjon, sedErEndring);

        T medlemskap = hentMedlemskap(sed);

        MelosysEessiMelding.SvarAnmodningUnntak svarAnmodningUnntak = new MelosysEessiMelding.SvarAnmodningUnntak();
        svarAnmodningUnntak.setBeslutning(hentBeslutning(medlemskap));
        svarAnmodningUnntak.setBegrunnelse(hentBegrunnelse(medlemskap));
        svarAnmodningUnntak.setDelvisInnvilgetPeriode(hentDelvisInnvilgetPeriode(medlemskap));

        melosysEessiMelding.setSvarAnmodningUnntak(svarAnmodningUnntak);
        return melosysEessiMelding;
    }

    abstract T hentMedlemskap(SED sed);

    abstract MelosysEessiMelding.Beslutning hentBeslutning(T medlemskap);

    abstract String hentBegrunnelse(T medlemskap);

    abstract Periode hentDelvisInnvilgetPeriode(T medlemskap);
}
