package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding;

import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.models.sed.SED;

public class MelosysEessiMeldingMapperX006 implements MelosysEessiMeldingMapper {

    private final String rinaInstitusjonId;

    public MelosysEessiMeldingMapperX006(String rinaInstitusjonId) {
        this.rinaInstitusjonId = rinaInstitusjonId;
    }

    @Override
    public MelosysEessiMelding map(String aktoerId, SED sed, String rinaDokumentID, String rinaSaksnummer,
                                   String sedType, String bucType, String avsenderID,
                                   String landkode, String journalpostID, String dokumentID, String gsakSaksnummer,
                                   boolean sedErEndring, String sedVersjon) {
        MelosysEessiMelding melosysEessiMelding = MelosysEessiMeldingMapper.super.map(aktoerId, sed, rinaDokumentID,
            rinaSaksnummer, sedType, bucType, avsenderID, landkode, journalpostID, dokumentID, gsakSaksnummer,
            sedErEndring, sedVersjon);

        melosysEessiMelding.setX006NavErFjernet(inneholderOgErNorskInstitusjon(sed));

        return melosysEessiMelding;
    }

    private boolean inneholderOgErNorskInstitusjon(SED sed) {
        return sed.getNav() != null
            && sed.getNav().getSak() != null
            && sed.getNav().getSak().getFjerninstitusjon() != null
            && sed.getNav().getSak().getFjerninstitusjon().getInstitusjon() != null
            && sed.getNav().getSak().getFjerninstitusjon().getInstitusjon().getId() != null
            && sed.getNav().getSak().getFjerninstitusjon().getInstitusjon().getId().equals(rinaInstitusjonId);
    }
}
