package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding;

import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.kafka.producers.model.Periode;
import no.nav.melosys.eessi.kafka.producers.model.SvarAnmodningUnntak;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.SvarAnmodningUnntakBeslutning;

public abstract class SvarAnmodningUnntakEessiMeldingMapper<T extends Medlemskap> implements MelosysEessiMeldingMapper {

    @Override
    public MelosysEessiMelding map(String aktoerId, SED sed, Integer sequenceId, String rinaDokumentID, String rinaSaksnummer,
                                   String sedType, String bucType, String avsenderID,
                                   String landkode, String journalpostID, String dokumentID, String gsakSaksnummer,
                                   boolean sedErEndring, String sedVersjon) {
        MelosysEessiMelding melosysEessiMelding = MelosysEessiMeldingMapper.super.map(aktoerId, sed, sequenceId, rinaDokumentID,
                rinaSaksnummer, sedType, bucType, avsenderID, landkode, journalpostID, dokumentID, gsakSaksnummer,
                sedErEndring, sedVersjon);

        T medlemskap = hentMedlemskap(sed);

        SvarAnmodningUnntak svarAnmodningUnntak = new SvarAnmodningUnntak();
        svarAnmodningUnntak.setBeslutning(hentBeslutning(medlemskap));
        svarAnmodningUnntak.setBegrunnelse(hentBegrunnelse(medlemskap));
        svarAnmodningUnntak.setDelvisInnvilgetPeriode(hentDelvisInnvilgetPeriode(medlemskap));

        melosysEessiMelding.setSvarAnmodningUnntak(svarAnmodningUnntak);
        return melosysEessiMelding;
    }

    abstract T hentMedlemskap(SED sed);

    abstract SvarAnmodningUnntakBeslutning hentBeslutning(T medlemskap);

    abstract String hentBegrunnelse(T medlemskap);

    abstract Periode hentDelvisInnvilgetPeriode(T medlemskap);
}
