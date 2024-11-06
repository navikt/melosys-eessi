package no.nav.melosys.eessi.service.sed.mapper.til_sed.administrativ

import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.nav.*
import no.nav.melosys.eessi.service.sed.mapper.til_sed.SedMapper

class X008Mapper : SedMapper {
    override fun mapTilSed(sedData: SedDataDto, erCDM4_3: Boolean): SED {
        val sed = super.mapTilSed(sedData, erCDM4_3)

        val sakForSed = mapSak(sedData, sed!!)
        sed.nav!!.sak = sakForSed

        return sed
    }

    fun mapSak(sedData: SedDataDto, sed: SED): Sak {
        val sak = Sak()
        val invalideringSed = InvalideringSed()

        invalideringSed.type = sedData.invalideringSedDto!!.sedTypeSomSkalInvalideres
        invalideringSed.utstedelsesdato = sedData.invalideringSedDto!!.utstedelsedato
        // 04  = The case was reconsidered and the grounds for the invalidated SED are no longer valid
        invalideringSed.grunn = Grunn("04", "")

        sak.ugyldiggjoere = Ugyldiggjoere(invalideringSed)
        sak.kontekst = mapKontekst(sed)

        return sak
    }

    override fun getSedType(): SedType {
        return SedType.X008
    }
    private fun mapKontekst(sed: SED): Kontekst {
        val kontekst = Kontekst()
        kontekst.bruker = sed.nav!!.bruker
        return kontekst
    }
}
