package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.exception.MappingException
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA004
import no.nav.melosys.eessi.models.sed.nav.Avslag
import no.nav.melosys.eessi.models.sed.nav.Land

class A004Mapper : LovvalgSedMapper<MedlemskapA004> {

    override fun getSedType() = SedType.A004

    override fun getMedlemskap(sedData: SedDataDto): MedlemskapA004 {
        val utpekingAvvis = sedData.utpekingAvvis
            ?: throw MappingException("Trenger UtpekingAvvis for å opprette A004")

        return MedlemskapA004(
            avslag = Avslag(
                erbehovformerinformasjon = if (utpekingAvvis.vilSendeAnmodningOmMerInformasjon) "ja" else "nei",
                forslagformedlemskap = utpekingAvvis.nyttLovvalgsland?.let { Land(landkode = it) },
                begrunnelse = utpekingAvvis.begrunnelseUtenlandskMyndighet
            )
        )
    }
}
