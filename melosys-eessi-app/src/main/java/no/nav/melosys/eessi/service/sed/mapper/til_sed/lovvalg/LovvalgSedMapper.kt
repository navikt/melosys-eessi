package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.controller.dto.VedtakDto
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap
import no.nav.melosys.eessi.models.sed.nav.Vedtak
import no.nav.melosys.eessi.service.sed.mapper.til_sed.SedMapper

/**
 * Felles mapper-interface for alle typer av lovvalgs-SED'er. Mapper NAV-objektet i NAV-SED, som brukes av eux for
 * å plukke ut nødvendig informasjon for en angitt SED
 */
interface LovvalgSedMapper<T : Medlemskap?> : SedMapper {
    override fun mapTilSed(sedData: SedDataDto, erCDM4_3: Boolean): SED {
        val sed = super.mapTilSed(sedData, erCDM4_3)
        sed!!.medlemskap = getMedlemskap(sedData)

        return sed
    }

    fun setVedtaksdata(vedtak: Vedtak, vedtakDto: VedtakDto?) {
        if (vedtakDto != null && !vedtakDto.erFørstegangsvedtak) {
            vedtak.erendringsvedtak = "nei"
            vedtak.datoforrigevedtak = if (vedtakDto.datoForrigeVedtak != null) vedtakDto.datoForrigeVedtak.toString() else null
        } else {
            vedtak.eropprinneligvedtak = "ja"
        }
    }

    fun getMedlemskap(sedData: SedDataDto): T
}
