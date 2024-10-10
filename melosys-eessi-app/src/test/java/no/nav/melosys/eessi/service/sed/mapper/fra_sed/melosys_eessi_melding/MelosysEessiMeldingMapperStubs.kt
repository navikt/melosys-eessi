package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap
import no.nav.melosys.eessi.models.sed.nav.Bruker
import no.nav.melosys.eessi.models.sed.nav.Nav
import no.nav.melosys.eessi.models.sed.nav.Person
import no.nav.melosys.eessi.models.sed.nav.Statsborgerskap

object MelosysEessiMeldingMapperStubs {

    fun createSed(medlemskap: Medlemskap?): SED {
        val sed = SED().apply {
            this.medlemskap = medlemskap
            nav = Nav().apply {
                bruker = Bruker().apply {
                    person = Person().apply {
                        statsborgerskap = listOf(Statsborgerskap().apply { land = "SE" })
                    }
                }
            }
        }
        return sed
    }

    fun createSedHendelse(): SedHendelse {
        return SedHendelse.builder()
            .navBruker("navbruker")
            .rinaDokumentId("rinadok")
            .rinaSakId("rinasak")
            .avsenderId("avsenderid")
            .avsenderNavn("avsendernavn")
            .bucType("buc")
            .sedType("sed")
            .id(1L)
            .build()
    }

    fun createSakInformasjon(): SakInformasjon {
        return SakInformasjon("journalpost", "dokument", "123")
    }

    data class SakInformasjon(
        val journalpostId: String,
        val dokumentId: String,
        val gsakSaksnummer: String
    )
}
