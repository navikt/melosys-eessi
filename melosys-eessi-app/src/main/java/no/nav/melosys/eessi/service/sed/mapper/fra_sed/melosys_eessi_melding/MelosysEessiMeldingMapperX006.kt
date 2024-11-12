package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding
import no.nav.melosys.eessi.models.sed.SED

class MelosysEessiMeldingMapperX006(private val rinaInstitusjonId: String) : MelosysEessiMeldingMapper {

    override fun map(eessiMeldingParams: EessiMeldingParams): MelosysEessiMelding =
        super.map(eessiMeldingParams).apply {
            x006NavErFjernet = inneholderOgErNorskInstitusjon(eessiMeldingParams.sed)
        }

    private fun inneholderOgErNorskInstitusjon(sed: SED): Boolean =
        sed.nav?.sak?.fjerninstitusjon?.institusjon?.id == rinaInstitusjonId}
