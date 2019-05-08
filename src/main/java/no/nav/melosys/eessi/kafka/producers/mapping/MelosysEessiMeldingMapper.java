package no.nav.melosys.eessi.kafka.producers.mapping;

import java.util.List;
import java.util.stream.Collectors;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiMelding;
import no.nav.melosys.eessi.kafka.producers.Periode;
import no.nav.melosys.eessi.kafka.producers.Statsborgerskap;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.joark.SakInformasjon;

public abstract class MelosysEessiMeldingMapper {

    public MelosysEessiMelding map(String aktoerId, SED sed, SedHendelse sedHendelse, SakInformasjon sakInformasjon) {
        MelosysEessiMelding melosysEessiMelding = new MelosysEessiMelding();
        melosysEessiMelding.setSedId(sedHendelse.getRinaDokumentId());
        melosysEessiMelding.setRinaSaksnummer(sedHendelse.getRinaSakId());
        melosysEessiMelding.setJournalpostId(sakInformasjon.getJournalpostId());
        melosysEessiMelding.setDokumentId(sakInformasjon.getDokumentId());
        melosysEessiMelding.setGsakSaksnummer(Long.parseLong(sakInformasjon.getGsakSaksnummer()));
        melosysEessiMelding.setAktoerId(aktoerId);

        melosysEessiMelding.setStatsborgerskap(
                mapStatsborgerskap(sed.getNav().getBruker().getPerson().getStatsborgerskap())
        );

        melosysEessiMelding.setPeriode(mapPeriode(sed));

        melosysEessiMelding.setLovvalgsland(hentLovvalgsland(sed));
        melosysEessiMelding.setArtikkel(hentLovvalgsbestemmelse(sed));
        melosysEessiMelding.setErEndring(sedErEndring(sed));

        return melosysEessiMelding;
    }

    private List<Statsborgerskap> mapStatsborgerskap(List<no.nav.melosys.eessi.models.sed.nav.Statsborgerskap> statsborgerskapListe) {
        return statsborgerskapListe.stream().map(s -> {
            Statsborgerskap statsborgerskap = new Statsborgerskap();
            statsborgerskap.setLandkode(s.getLand());
            return statsborgerskap;
        }).collect(Collectors.toList());
    }

    abstract Periode mapPeriode(SED sed);
    abstract String hentLovvalgsland(SED sed);
    abstract String hentLovvalgsbestemmelse(SED sed);
    abstract Boolean sedErEndring(SED sed);

}
