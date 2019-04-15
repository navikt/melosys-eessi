package no.nav.melosys.eessi.kafka.producers;

import java.util.List;
import java.util.stream.Collectors;
import no.nav.melosys.eessi.avro.MelosysEessiMelding;
import no.nav.melosys.eessi.avro.Statsborgerskap;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.SedType;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;
import no.nav.melosys.eessi.models.sed.nav.AapenPeriode;
import no.nav.melosys.eessi.models.sed.nav.Fastperiode;
import no.nav.melosys.eessi.models.sed.nav.Periode;
import no.nav.melosys.eessi.service.joark.SakInformasjon;

public class MelosysEessiMeldingMapper {

    public static MelosysEessiMelding map(String aktoerId, SED sed, SedHendelse sedHendelse, SakInformasjon sakInformasjon) {
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

    private static Boolean sedErEndring(SED sed) {
        SedType sedType = SedType.valueOf(sed.getSed());
        if (sedType.equals(SedType.A009)) {
            MedlemskapA009 medlemskapA009 = (MedlemskapA009) sed.getMedlemskap();
            return "ja".equalsIgnoreCase(medlemskapA009.getVedtak().getErendringsvedtak());
        }
        return false;
    }

    private static String hentLovvalgsbestemmelse(SED sed) {
        SedType sedType = SedType.valueOf(sed.getSed());
        if (sedType.equals(SedType.A009)) {
            MedlemskapA009 medlemskapA009 = (MedlemskapA009) sed.getMedlemskap();
            return medlemskapA009.getVedtak().getArtikkelforordning();
        }

        return null;
    }

    private static String hentLovvalgsland(SED sed) {
        SedType sedType = SedType.valueOf(sed.getSed());
        if (sedType.equals(SedType.A009)) {
            MedlemskapA009 medlemskapA009 = (MedlemskapA009) sed.getMedlemskap();
            return medlemskapA009.getVedtak().getLand();
        }

        return null;
    }

    private static no.nav.melosys.eessi.avro.Periode mapPeriode(SED sed) {

        String fom, tom;
        Fastperiode fastperiode = null;
        AapenPeriode aapenPeriode = null;

        SedType sedType = SedType.valueOf(sed.getSed());
        if (sedType.equals(SedType.A009)) {
            MedlemskapA009 medlemskapA009 = (MedlemskapA009) sed.getMedlemskap();
            Periode periode = medlemskapA009.getVedtak().getGjelderperiode();
            if (periode.getAapenperiode() != null && periode.getAapenperiode().getStartdato() != null) {
                aapenPeriode = periode.getAapenperiode();
            } else {
                fastperiode = periode.getFastperiode();
            }
        }

        if (fastperiode != null) {
            fom = fastperiode.getStartdato();
            tom = fastperiode.getSluttdato();
        } else {
            fom = aapenPeriode.getStartdato();
            tom = null;
        }

        return new no.nav.melosys.eessi.avro.Periode(fom, tom);
    }

    private static List<Statsborgerskap> mapStatsborgerskap(List<no.nav.melosys.eessi.models.sed.nav.Statsborgerskap> statsborgerskap) {
        return statsborgerskap.stream().map(s -> {
            Statsborgerskap avroStatsborgerskap = new Statsborgerskap();
            avroStatsborgerskap.setLandkode(s.getLand());
            return avroStatsborgerskap;
        }).collect(Collectors.toList());
    }

    public static boolean isSupportedSed(SED sed) {
        SedType sedType = SedType.valueOf(sed.getSed());
        return sedType == SedType.A009;
    }
}
