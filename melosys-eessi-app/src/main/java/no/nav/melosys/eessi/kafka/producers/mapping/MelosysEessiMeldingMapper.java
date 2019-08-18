package no.nav.melosys.eessi.kafka.producers.mapping;

import java.util.List;
import java.util.stream.Collectors;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiMelding;
import no.nav.melosys.eessi.kafka.producers.Statsborgerskap;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.joark.SakInformasjon;

public interface MelosysEessiMeldingMapper {

    default MelosysEessiMelding map(String aktoerId, SED sed, SedHendelse sedHendelse, SakInformasjon sakInformasjon, boolean sedErEndring) {
        return map(aktoerId, sed, sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId(), sedHendelse.getSedType(),
                sedHendelse.getBucType(), sakInformasjon.getJournalpostId(), sakInformasjon.getDokumentId(),
                sakInformasjon.getGsakSaksnummer(), sedErEndring);
    }

    default MelosysEessiMelding map(String aktoerId, SED sed, String rinaDokumentID, String rinaSaksnummer,
            String sedType, String bucType, String journalpostID, String dokumentID, String gsakSaksnummer, boolean sedErEndring) {
        MelosysEessiMelding melosysEessiMelding = new MelosysEessiMelding();
        melosysEessiMelding.setSedId(rinaDokumentID);
        melosysEessiMelding.setRinaSaksnummer(rinaSaksnummer);
        melosysEessiMelding.setJournalpostId(journalpostID);
        melosysEessiMelding.setDokumentId(dokumentID);
        melosysEessiMelding.setGsakSaksnummer(gsakSaksnummer != null ? Long.parseLong(gsakSaksnummer) : null);
        melosysEessiMelding.setAktoerId(aktoerId);
        melosysEessiMelding.setYtterligereInformasjon(sed.getNav().getYtterligereinformasjon());

        melosysEessiMelding.setSedType(sedType);
        melosysEessiMelding.setBucType(bucType);

        melosysEessiMelding.setStatsborgerskap(
                mapStatsborgerskap(sed.getNav().getBruker().getPerson().getStatsborgerskap())
        );
        return melosysEessiMelding;
    }

    default List<Statsborgerskap> mapStatsborgerskap(List<no.nav.melosys.eessi.models.sed.nav.Statsborgerskap> statsborgerskapListe) {
        return statsborgerskapListe.stream().map(no.nav.melosys.eessi.models.sed.nav.Statsborgerskap::getLand)
                .map(Statsborgerskap::new).collect(Collectors.toList());
    }
}
