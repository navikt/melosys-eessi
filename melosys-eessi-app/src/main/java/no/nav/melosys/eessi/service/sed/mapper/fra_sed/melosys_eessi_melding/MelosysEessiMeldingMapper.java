package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding;

import no.nav.melosys.eessi.kafka.producers.model.Arbeidssted;
import no.nav.melosys.eessi.kafka.producers.model.Avsender;
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.kafka.producers.model.Statsborgerskap;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public interface MelosysEessiMeldingMapper {

    default MelosysEessiMelding map(String aktoerId, SED sed, UUID batchID, String rinaDokumentID, String rinaSaksnummer,
                                    String sedType, String bucType, String avsenderID, String landkode,
                                    String journalpostID, String dokumentID, String gsakSaksnummer,
                                    boolean sedErEndring, String sedVersjon) {
        var melosysEessiMelding = new MelosysEessiMelding();
        melosysEessiMelding.setAktoerId(aktoerId);
        melosysEessiMelding.setBatchID(batchID);
        melosysEessiMelding.setSedId(rinaDokumentID);
        melosysEessiMelding.setRinaSaksnummer(rinaSaksnummer);
        melosysEessiMelding.setSedType(sedType);
        melosysEessiMelding.setBucType(bucType);
        melosysEessiMelding.setAvsender(new Avsender(avsenderID, LandkodeMapper.mapTilNavLandkode(landkode)));
        melosysEessiMelding.setJournalpostId(journalpostID);
        melosysEessiMelding.setDokumentId(dokumentID);
        melosysEessiMelding.setGsakSaksnummer(gsakSaksnummer != null ? Long.parseLong(gsakSaksnummer) : null);
        melosysEessiMelding.setYtterligereInformasjon(sed.getNav().getYtterligereinformasjon());


        if (inneholderStatsborgerskap(sed)) {
            melosysEessiMelding.setStatsborgerskap(
                mapStatsborgerskap(sed.getNav().getBruker().getPerson().hentStatsborgerksapsliste())
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


    default List<Statsborgerskap> mapStatsborgerskap(Collection<String> statsborgerskapListe) {
        return statsborgerskapListe.stream().map(Statsborgerskap::new).collect(Collectors.toList());
    }
}
