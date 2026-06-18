package no.nav.melosys.eessi.integration.sak

data class SakDto(
    var id: Long? = null,
    var tema: String? = null, // https://kodeverkviewer.adeo.no/kodeverk/xml/fagomrade.xml
    var applikasjon: String? = null, // Fagsystemkode for applikasjon
    var fagsakNr: String? = null, // Fagsaknr for den aktuelle saken
    var aktoerId: String? = null, // Id til aktøren saken gjelder
    var orgnr: String? = null, // Orgnr til foretaket saken gjelder
    var opprettetAv: String? = null, // Brukerident til den som opprettet saken
    var opprettetTidspunkt: String? = null // Lagres som LocalDateTime i Sak API, men eksponeres som ZonedDateTime
)
