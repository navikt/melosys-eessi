package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding
import no.nav.melosys.eessi.models.sed.SED

class MelosysEessiMeldingMapperX006(private val rinaInstitusjonId: String) : MelosysEessiMeldingMapper {
    override fun map(
        aktoerId: String?,
        sed: SED?,
        rinaDokumentID: String?,
        rinaSaksnummer: String?,
        sedType: String?,
        bucType: String?,
        avsenderID: String?,
        landkode: String?,
        journalpostID: String?,
        dokumentID: String?,
        gsakSaksnummer: String?,
        sedErEndring: Boolean,
        sedVersjon: String?
    ): MelosysEessiMelding {
        val melosysEessiMelding = super.map(
            aktoerId, sed, rinaDokumentID,
            rinaSaksnummer, sedType, bucType, avsenderID, landkode, journalpostID, dokumentID, gsakSaksnummer,
            sedErEndring, sedVersjon
        )

        melosysEessiMelding.x006NavErFjernet = inneholderOgErNorskInstitusjon(sed!!)

        return melosysEessiMelding
    }

    private fun inneholderOgErNorskInstitusjon(sed: SED): Boolean {
        return sed.nav != null
            && sed.nav!!.sak != null
            && sed.nav!!.sak!!.fjerninstitusjon != null
            && sed.nav!!.sak!!.fjerninstitusjon!!.institusjon != null
            && sed.nav!!.sak!!.fjerninstitusjon!!.institusjon!!.id != null
            && sed.nav!!.sak!!.fjerninstitusjon!!.institusjon!!.id == rinaInstitusjonId
    }
}
