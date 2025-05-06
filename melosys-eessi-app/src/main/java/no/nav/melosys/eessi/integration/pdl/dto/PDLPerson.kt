package no.nav.melosys.eessi.integration.pdl.dto

data class PDLPerson(
    var navn: List<PDLNavn> = emptyList(),
    var foedselsdato: List<PDLFoedsel> = emptyList(),
    var statsborgerskap: List<PDLStatsborgerskap> = emptyList(),
    var folkeregisterpersonstatus: List<PDLFolkeregisterPersonstatus> = emptyList(),
    var utenlandskIdentifikasjonsnummer: List<PDLUtenlandskIdentifikator> = emptyList(),
    var kjoenn: List<PDLKjoenn> = emptyList()
)
