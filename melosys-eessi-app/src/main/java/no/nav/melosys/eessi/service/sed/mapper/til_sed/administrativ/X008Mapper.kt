package no.nav.melosys.eessi.service.sed.mapper.til_sed.administrativ

import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.exception.MappingException
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.nav.*
import no.nav.melosys.eessi.service.sed.mapper.til_sed.SedMapper

class X008Mapper : SedMapper {
    override fun getSedType(): SedType = SedType.X008

    override fun mapTilSed(sedData: SedDataDto): SED =
        super.mapTilSed(sedData).also { sed ->
            sed.nav?.let { nav ->
                nav.sak = mapSak(sedData, sed)
            } ?: throw MappingException("nav.sak er påkrevd for X008")
        }

    fun mapSak(sedData: SedDataDto, sed: SED): Sak {
        val invalideringSedDto = sedData.invalideringSedDto
            ?: throw IllegalArgumentException("SedDataDto.invalideringSedDto kan ikke være null")

        return Sak(
            ugyldiggjoere = Ugyldiggjoere(
                sed = InvalideringSed(
                    type = invalideringSedDto.sedTypeSomSkalInvalideres,
                    utstedelsesdato = invalideringSedDto.utstedelsedato,
                    grunn = Grunn("04", "") // 04  = The case was reconsidered and the grounds for the invalidated SED are no longer valid
                )
            ),
            kontekst = mapKontekst(sed)
        )
    }

    private fun mapKontekst(sed: SED) = Kontekst(
        bruker = sed.nav?.bruker ?: throw MappingException("nav.bruker kan ikke være null")
    )
}
