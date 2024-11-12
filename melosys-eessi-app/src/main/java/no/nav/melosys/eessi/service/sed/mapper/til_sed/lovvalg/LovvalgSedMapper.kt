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
interface LovvalgSedMapper<T : Medlemskap> : SedMapper {
    fun getMedlemskap(sedData: SedDataDto): T

    override fun mapTilSed(sedData: SedDataDto, erCDM4_3: Boolean): SED =
        super.mapTilSed(sedData, erCDM4_3).apply {
            medlemskap = getMedlemskap(sedData)
        }

    fun setVedtaksdata(vedtak: Vedtak, vedtakDto: VedtakDto?) =
        if (vedtakDto != null && !vedtakDto.erFørstegangsvedtak) {
            vedtak.erendringsvedtak = "nei"
            vedtak.datoforrigevedtak = vedtakDto.datoForrigeVedtak?.toString()
        } else {
            vedtak.eropprinneligvedtak = "ja"
        }
}
