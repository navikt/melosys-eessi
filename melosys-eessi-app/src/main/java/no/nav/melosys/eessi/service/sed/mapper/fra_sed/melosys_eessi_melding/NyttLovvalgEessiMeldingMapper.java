package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding;

import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.kafka.producers.model.Periode;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.NyttLovvalgSedMapper;

public interface NyttLovvalgEessiMeldingMapper<T extends Medlemskap> extends NyttLovvalgSedMapper<T>, MelosysEessiMeldingMapper {
    @Override
    default MelosysEessiMelding map(String aktoerId, SED sed, String rinaDokumentID, String rinaSaksnummer,
                                    String sedType, String bucType, String avsenderID,
                                    String landkode, String journalpostID, String dokumentID, String gsakSaksnummer,
                                    boolean sedErEndring, String sedVersjon) {
        MelosysEessiMelding melosysEessiMelding = MelosysEessiMeldingMapper.super.map(aktoerId, sed, rinaDokumentID,
            rinaSaksnummer, sedType, bucType, avsenderID, landkode, journalpostID, dokumentID, gsakSaksnummer,
            sedErEndring, sedVersjon);

        T medlemskap = hentMedlemskap(sed);

        melosysEessiMelding.setPeriode(mapPeriode(medlemskap));

        melosysEessiMelding.setLovvalgsland(hentLovvalgsland(medlemskap));
        melosysEessiMelding.setArtikkel(hentLovvalgsbestemmelse(medlemskap));
        melosysEessiMelding.setErEndring(sedErEndring || sedErEndring(medlemskap));
        melosysEessiMelding.setMidlertidigBestemmelse(erMidlertidigBestemmelse(medlemskap));
        melosysEessiMelding.setAnmodningUnntak(hentAnmodningUnntak(medlemskap));

        return melosysEessiMelding;
    }

    Periode mapPeriode(T medlemskap);
}
