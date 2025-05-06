package no.nav.melosys.eessi.integration.pdl.dto

data class PDLPerson(
    val navn: List<PDLNavn> = emptyList(),
    val foedselsdato: List<PDLFoedsel> = emptyList(),
    val statsborgerskap: List<PDLStatsborgerskap> = emptyList(),
    val folkeregisterpersonstatus: List<PDLFolkeregisterPersonstatus> = emptyList(),
    val utenlandskIdentifikasjonsnummer: List<PDLUtenlandskIdentifikator> = emptyList(),
    val kjoenn: List<PDLKjoenn> = emptyList()
)
