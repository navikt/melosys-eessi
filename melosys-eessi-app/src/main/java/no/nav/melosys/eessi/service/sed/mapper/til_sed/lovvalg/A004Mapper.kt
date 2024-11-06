package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.exception.MappingException
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA004
import no.nav.melosys.eessi.models.sed.nav.Avslag
import no.nav.melosys.eessi.models.sed.nav.Land

class A004Mapper : LovvalgSedMapper<MedlemskapA004> {
    override fun getMedlemskap(sedData: SedDataDto): MedlemskapA004 {
        val utpekingAvvis = sedData.utpekingAvvis
            ?: throw MappingException("Trenger UtpekingAvvis for å opprette A004")

        val medlemskap = MedlemskapA004()
        medlemskap.avslag = getAvslag(
            utpekingAvvis.vilSendeAnmodningOmMerInformasjon,
            utpekingAvvis.nyttLovvalgsland,
            utpekingAvvis.begrunnelseUtenlandskMyndighet
        )

        return medlemskap
    }

    private fun getAvslag(vilSendeAnmodningOmMerInformasjon: Boolean, nyttLovvalgsland: String?, begrunnelseUtenlandskMyndighet: String?): Avslag {
        val avslag = Avslag()
        avslag.erbehovformerinformasjon = if (vilSendeAnmodningOmMerInformasjon) "ja" else "nei"
        if (nyttLovvalgsland != null) {
            val lovvalgsland = Land()
            lovvalgsland.landkode = nyttLovvalgsland
            avslag.forslagformedlemskap = lovvalgsland
        }
        avslag.begrunnelse = begrunnelseUtenlandskMyndighet
        return avslag
    }

    override fun getSedType() = SedType.A004

}
