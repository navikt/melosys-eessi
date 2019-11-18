package no.nav.melosys.eessi.kafka.producers.mapping;

import java.time.LocalDate;
import no.nav.melosys.eessi.kafka.producers.model.AnmodningUnntak;
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.kafka.producers.model.Periode;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.models.sed.nav.AapenPeriode;
import no.nav.melosys.eessi.models.sed.nav.PeriodeA010;

import static no.nav.melosys.eessi.models.DatoUtils.tilLocalDate;

public abstract class NyttLovvalgEessiMeldingMapper<T extends Medlemskap> implements MelosysEessiMeldingMapper {

    @Override
    public MelosysEessiMelding map(String aktoerId, SED sed, String rinaDokumentID, String rinaSaksnummer,
            String sedType, String bucType, String journalpostID, String dokumentID, String gsakSaksnummer, boolean sedErEndring) {
        MelosysEessiMelding melosysEessiMelding = MelosysEessiMeldingMapper.super.map(aktoerId, sed, rinaDokumentID,
                rinaSaksnummer, sedType, bucType, journalpostID, dokumentID, gsakSaksnummer, sedErEndring);

        T medlemskap = hentMedlemskap(sed);

        melosysEessiMelding.setPeriode(mapPeriode(medlemskap));

        melosysEessiMelding.setLovvalgsland(hentLovvalgsland(medlemskap));
        melosysEessiMelding.setArtikkel(hentLovvalgsbestemmelse(medlemskap));
        melosysEessiMelding.setErEndring(sedErEndring || sedErEndring(medlemskap));
        melosysEessiMelding.setMidlertidigBestemmelse(erMidlertidigBestemmelse(medlemskap));
        melosysEessiMelding.setAnmodningUnntak(hentAnmodningUnntak(medlemskap));

        return melosysEessiMelding;
    }

    abstract Periode mapPeriode(T medlemskap);
    abstract String hentLovvalgsland(T medlemskap);
    abstract String hentLovvalgsbestemmelse(T medlemskap);

    AnmodningUnntak hentAnmodningUnntak(T medlemskap) {
        return null;
    }

    abstract Boolean sedErEndring(T medlemskap);
    abstract T hentMedlemskap(SED sed);

    boolean erMidlertidigBestemmelse(T medlemskap) {
        return false;
    }

    Periode hentPeriode(PeriodeA010 periode) {
        LocalDate fom;
        LocalDate tom;

        if (periode.erAapenPeriode()) {
            AapenPeriode aapenPeriode = periode.getAapenperiode();
            fom = tilLocalDate(aapenPeriode.getStartdato());
            tom = null;
        } else {
            fom = tilLocalDate(periode.getStartdato());
            tom = tilLocalDate(periode.getSluttdato());
        }
        return new Periode(fom, tom);
    }
}
