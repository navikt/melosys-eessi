package no.nav.melosys.eessi.kafka.producers.mapping;

import java.util.List;
import java.util.stream.Collectors;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiMelding;
import no.nav.melosys.eessi.kafka.producers.Periode;
import no.nav.melosys.eessi.kafka.producers.Statsborgerskap;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.models.sed.nav.PeriodeA010;
import no.nav.melosys.eessi.service.joark.SakInformasjon;

public abstract class MelosysEessiMeldingMapper<T extends Medlemskap> {

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

        T medlemskap = hentMedlemskap(sed);

        melosysEessiMelding.setPeriode(mapPeriode(medlemskap));

        melosysEessiMelding.setLovvalgsland(hentLovvalgsland(medlemskap));
        melosysEessiMelding.setArtikkel(hentLovvalgsbestemmelse(medlemskap));
        melosysEessiMelding.setErEndring(sedErEndring(medlemskap));
        melosysEessiMelding.setMidlertidigBestemmelse(erMidlertidigBestemmelse(medlemskap));

        melosysEessiMelding.setBucType(sedHendelse.getBucType());
        melosysEessiMelding.setSedType(sedHendelse.getSedType());

        return melosysEessiMelding;
    }

    private List<Statsborgerskap> mapStatsborgerskap(List<no.nav.melosys.eessi.models.sed.nav.Statsborgerskap> statsborgerskapListe) {
        return statsborgerskapListe.stream().map(s -> {
            Statsborgerskap statsborgerskap = new Statsborgerskap();
            statsborgerskap.setLandkode(s.getLand());
            return statsborgerskap;
        }).collect(Collectors.toList());
    }

    abstract Periode mapPeriode(T medlemskap);
    abstract String hentLovvalgsland(T medlemskap);
    abstract String hentLovvalgsbestemmelse(T medlemskap);
    abstract Boolean sedErEndring(T medlemskap);
    abstract T hentMedlemskap(SED sed);

    boolean erMidlertidigBestemmelse(T medlemskap) {
        return false;
    }

    Periode hentPeriode(PeriodeA010 periode) {
        String fom;
        String tom;

        if (periode.erAapenPeriode()) {
            fom = periode.getAapenperiode().getStartdato();
            tom = null;
        } else {
            fom = periode.getStartdato();
            tom = periode.getSluttdato();
        }
        return new Periode(fom, tom);
    }
}
