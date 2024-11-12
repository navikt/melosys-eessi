package no.nav.melosys.eessi.controller.dto

import no.nav.melosys.eessi.models.sed.nav.Adresse as RinaAdresse
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper
import org.apache.commons.lang3.StringUtils

data class Adresse(
    var poststed: String? = null,
    var postnr: String? = null,
    var land: String? = null, // kaster NullPointerException i java kode, men av må kunne lage object med null-verdier
    var gateadresse: String? = null,
    var tilleggsnavn: String? = null,
    var region: String? = null,
    var adressetype: Adressetype? = null
) {
    companion object {
        fun av(adresseFraRina: RinaAdresse?): Adresse = adresseFraRina?.let {
            Adresse(
                poststed = it.by,
                postnr = it.postnummer,
                land = LandkodeMapper.mapTilNavLandkode(it.land),
                gateadresse = "${StringUtils.defaultIfEmpty(it.gate, "")} ${StringUtils.defaultIfEmpty(it.bygning, "")}".trim(),
                tilleggsnavn = StringUtils.defaultIfEmpty(it.bygning, ""),
                region = it.region,
                adressetype = Adressetype.fraAdressetypeRina(it.type)
            )
        } ?: Adresse()
    }
}
