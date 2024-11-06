package no.nav.melosys.eessi.service.sed.mapper.til_sed.administrativ

import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.Konstanter
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.nav.*
import java.time.LocalDate

class X001Mapper : AdministrativSedMapper {

    fun mapFraSed(sed: SED, aarsak: String, erCDM4_3: Boolean): SED {
        return SED(
            sedType = SedType.X001.toString(),
            sedGVer = Konstanter.DEFAULT_SED_G_VER,
            sedVer = if (erCDM4_3) Konstanter.SED_VER_CDM_4_3 else Konstanter.DEFAULT_SED_VER,
            nav = mapNav(sed, aarsak)
        )
    }

    private fun mapNav(sed: SED, aarsak: String): Nav {
        return Nav(
            sak = mapSak(sed, aarsak)
        )
    }

    private fun mapSak(sed: SED, aarsak: String): Sak {
        return Sak(
            anmodning = mapAnmodning(aarsak),
            kontekst = mapKontekst(sed)
        )
    }

    private fun mapKontekst(sed: SED): Kontekst {
        return Kontekst(
            bruker = sed.nav?.bruker
        )
    }

    private fun mapAnmodning(aarsakType: String): X001Anmodning {
        val aarsak = Aarsak(type = aarsakType)
        val avslutning = Avslutning(
            dato = LocalDate.now().format(Konstanter.dateTimeFormatter),
            aarsak = aarsak,
            type = "automatisk"
        )
        return X001Anmodning(avslutning = avslutning)
    }

    override fun getSedType(): SedType {
        return SedType.X001
    }
}
