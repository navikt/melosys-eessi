package no.nav.melosys.eessi.service.sed.mapper.fra_sed

import no.nav.melosys.eessi.controller.dto.Periode
import no.nav.melosys.eessi.kafka.producers.model.AnmodningUnntak
import no.nav.melosys.eessi.models.DatoUtils.tilLocalDate
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap
import no.nav.melosys.eessi.models.sed.nav.PeriodeA010
import java.time.LocalDate

interface NyttLovvalgSedMapper<T : Medlemskap?> {
    fun hentLovvalgsland(medlemskap: T): String?

    fun hentLovvalgsbestemmelse(medlemskap: T): String?

    fun hentAnmodningUnntak(medlemskap: T): AnmodningUnntak? = null

    fun sedErEndring(medlemskap: T): Boolean

    fun hentMedlemskap(sed: SED): T

    fun erMidlertidigBestemmelse(medlemskap: T): Boolean = false

    fun hentPeriode(periode: PeriodeA010): Periode {
        val fom: LocalDate
        val tom: LocalDate?

        if (periode.erAapenPeriode()) {
            val aapenPeriode = periode.aapenperiode
            fom = tilLocalDate(aapenPeriode!!.startdato!!)
            tom = null
        } else {
            fom = tilLocalDate(periode.startdato!!)
            tom = tilLocalDate(periode.sluttdato!!)
        }

        return Periode(fom, tom)
    }
}
