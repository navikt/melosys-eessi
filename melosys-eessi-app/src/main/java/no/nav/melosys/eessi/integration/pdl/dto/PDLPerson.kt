package no.nav.melosys.eessi.integration.pdl.dto

data class PDLPerson(
    var navn: List<PDLNavn>? = null,
    var foedselsdato: List<PDLFoedsel>? = null,
    var statsborgerskap: List<PDLStatsborgerskap>? = null,
    var folkeregisterpersonstatus: List<PDLFolkeregisterPersonstatus>? = null,
    var utenlandskIdentifikasjonsnummer: List<PDLUtenlandskIdentifikator>? = null,
    var kjoenn: List<PDLKjoenn>? = null
)
