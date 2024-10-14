package no.nav.melosys.eessi.service.sed.mapper.fra_sed;

import java.time.LocalDate;

import no.nav.melosys.eessi.controller.dto.Periode;
import no.nav.melosys.eessi.kafka.producers.model.AnmodningUnntak;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.models.sed.nav.AapenPeriode;
import no.nav.melosys.eessi.models.sed.nav.PeriodeA010;

import static no.nav.melosys.eessi.models.DatoUtils.tilLocalDate;

public interface NyttLovvalgSedMapper<T extends Medlemskap> {

    String hentLovvalgsland(T medlemskap);

    String hentLovvalgsbestemmelse(T medlemskap);

    default AnmodningUnntak hentAnmodningUnntak(T medlemskap) {
        return null;
    }

    Boolean sedErEndring(T medlemskap);

    T hentMedlemskap(SED sed);

    default boolean erMidlertidigBestemmelse(T medlemskap) {
        return false;
    }

    default Periode hentPeriode(PeriodeA010 periode) {
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
