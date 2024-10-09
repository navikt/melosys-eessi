package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import no.nav.melosys.eessi.kafka.producers.model.AnmodningUnntak
import no.nav.melosys.eessi.kafka.producers.model.Periode
import no.nav.melosys.eessi.models.DatoUtils.tilLocalDate
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA001

internal class MelosysEessiMeldingMapperA001 : NyttLovvalgEessiMeldingMapper<MedlemskapA001> {
    override fun mapPeriode(medlemskap: MedlemskapA001?): Periode {
        return Periode(
            tilLocalDate(medlemskap!!.soeknadsperiode!!.startdato!!),
            tilLocalDate(medlemskap.soeknadsperiode!!.sluttdato!!)
        )
    }

    override fun hentLovvalgsland(medlemskap: MedlemskapA001): String? {
        return medlemskap.forespurtmedlemskap!!.iterator().next()!!.landkode
    }

    override fun hentLovvalgsbestemmelse(medlemskap: MedlemskapA001): String {
        return ARTIKKEL_16_1 // Denne er alltid 16.1
    }

    override fun hentAnmodningUnntak(medlemskap: MedlemskapA001): AnmodningUnntak {
        val anmodningUnntak = AnmodningUnntak()
        anmodningUnntak.unntakFraLovvalgsland = hentUnntakFraLovvalgsland(medlemskap)
        anmodningUnntak.unntakFraLovvalgsbestemmelse = hentUnntakFraLovvalgsbestemmelse(medlemskap)
        return anmodningUnntak
    }

    fun hentUnntakFraLovvalgsland(medlemskap: MedlemskapA001): String? {
        return medlemskap.naavaerendemedlemskap!!.iterator().next()!!.landkode
    }

    fun hentUnntakFraLovvalgsbestemmelse(medlemskap: MedlemskapA001): String? {
        return medlemskap.unntak!!.grunnlag!!.artikkel
    }

    override fun sedErEndring(medlemskap: MedlemskapA001): Boolean {
        if (medlemskap.anmodning == null) {
            return false
        }

        return "ja".equals(medlemskap.anmodning!!.erendring, ignoreCase = true)
    }

    override fun hentMedlemskap(sed: SED): MedlemskapA001? {
        return sed.medlemskap as MedlemskapA001?
    }

    companion object {
        private const val ARTIKKEL_16_1 = "16_1"
    }
}
