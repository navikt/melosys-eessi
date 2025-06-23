package no.nav.melosys.eessi.integration.eux.rina_api.dto

data class RinaSakOversiktV3(
    val fnr: String? = null,
    val fornavn: String? = null,
    val etternavn: String? = null,
    val foedselsdato: String? = null,
    val kjoenn: String? = null,
    val erSakseier: String? = null,
    val sakseier: Organisasjon? = null,
    val navinstitusjon: Organisasjon? = null,
    val sakTittel: String? = null,
    val sakType: String? = null,
    val sakId: String? = null,
    val internasjonalSakId: String? = null,
    val sakUrl: String? = null,
    val sistEndretDato: String? = null,
    val motparter: List<Motpart>? = null,
    val sakshandlinger: List<String>? = null,
    val sedListe: List<SedOversikt>? = null,
    val sensitiv: Boolean? = null,
    val cdmVersjon: String? = null
) 