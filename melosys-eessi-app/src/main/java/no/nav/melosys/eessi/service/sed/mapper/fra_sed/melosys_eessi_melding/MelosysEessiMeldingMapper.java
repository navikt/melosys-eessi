package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.melosys.eessi.kafka.producers.model.Arbeidssted;
import no.nav.melosys.eessi.kafka.producers.model.Avsender;
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.kafka.producers.model.Statsborgerskap;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;

public interface MelosysEessiMeldingMapper {

    default MelosysEessiMelding map(String aktoerId, SED sed, String rinaDokumentID, String rinaSaksnummer,
                                    String sedType, String bucType, String avsenderID, String landkode,
                                    String journalpostID, String dokumentID, String gsakSaksnummer,
                                    boolean sedErEndring, String sedVersjon) {
        MelosysEessiMelding melosysEessiMelding = new MelosysEessiMelding();
        melosysEessiMelding.setSedId(rinaDokumentID);
        melosysEessiMelding.setRinaSaksnummer(rinaSaksnummer);
        melosysEessiMelding.setAvsender(new Avsender(avsenderID, LandkodeMapper.mapTilNavLandkode(landkode)));
        melosysEessiMelding.setJournalpostId(journalpostID);
        melosysEessiMelding.setDokumentId(dokumentID);
        melosysEessiMelding.setGsakSaksnummer(gsakSaksnummer != null ? Long.parseLong(gsakSaksnummer) : null);
        melosysEessiMelding.setAktoerId(aktoerId);
        melosysEessiMelding.setYtterligereInformasjon(sed.getNav().getYtterligereinformasjon());

        melosysEessiMelding.setSedType(sedType);
        melosysEessiMelding.setBucType(bucType);

        if (inneholderStatsborgerskap(sed)) {
            melosysEessiMelding.setStatsborgerskap(
                    mapStatsborgerskap(sed.getNav().getBruker().getPerson().getStatsborgerskap())
            );
        }
        if (sed.getNav() != null && sed.getNav().getArbeidssted() != null) {
            melosysEessiMelding.setArbeidssteder(sed.getNav().getArbeidssted().stream().map(Arbeidssted::new).collect(Collectors.toList()));
        }
        melosysEessiMelding.setErEndring(sedErEndring);
        melosysEessiMelding.setSedVersjon(sedVersjon);
        return melosysEessiMelding;
    }

    default boolean inneholderStatsborgerskap(SED sed) {
        return sed.getNav() != null
                && sed.getNav().getBruker() != null
                && sed.getNav().getBruker().getPerson() != null
                && sed.getNav().getBruker().getPerson().getStatsborgerskap() != null;
    }

    default List<Statsborgerskap> mapStatsborgerskap(Collection<no.nav.melosys.eessi.models.sed.nav.Statsborgerskap> statsborgerskapListe) {
        return statsborgerskapListe.stream().map(no.nav.melosys.eessi.models.sed.nav.Statsborgerskap::getLand)
                .map(Statsborgerskap::new).collect(Collectors.toList());
    }
}
