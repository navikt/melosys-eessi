package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding;

import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.kafka.producers.model.Periode;
import no.nav.melosys.eessi.models.sed.SED;

public class AdministrativSedMapperX006 implements MelosysEessiMeldingMapper {

    @Override
    public MelosysEessiMelding map(String aktoerId, SED sed, String rinaDokumentID, String rinaSaksnummer,
                                   String sedType, String bucType, String avsenderID,
                                   String landkode, String journalpostID, String dokumentID, String gsakSaksnummer,
                                   boolean sedErEndring, String sedVersjon) {
        MelosysEessiMelding melosysEessiMelding = MelosysEessiMeldingMapper.super.map(aktoerId, sed, rinaDokumentID,
                rinaSaksnummer, sedType, bucType, avsenderID, landkode, journalpostID, dokumentID, gsakSaksnummer,
                sedErEndring, sedVersjon);

        melosysEessiMelding.setErMottaksInstitusjon(inneholderOgErNorskInstitusjon(sed));

        return melosysEessiMelding;
    }

    private boolean inneholderOgErNorskInstitusjon(SED sed) {
        return sed.getNav() != null
                && sed.getNav().getSak() != null
                && sed.getNav().getSak().getFjerninstitusjon() != null
                && sed.getNav().getSak().getFjerninstitusjon().getInstitusjon() != null
                && sed.getNav().getSak().getFjerninstitusjon().getInstitusjon().getId() != null
                && sed.getNav().getSak().getFjerninstitusjon().getInstitusjon().getId().split(":")[0].equals("NO");
    }
}
