package no.nav.melosys.eessi.service.sed

data class SedJournalføringMigreringRapportDto(
    var sedMottattMigreringRapportDtoList: MutableList<SedMottattMigreringRapportDto?>? = null,
    var sedSendtMigreringRapportDtoList: MutableList<SedSendtMigreringRapportDto?>? = null,
    var antallSedMottattHendelser: Int = 0,
    var antallSedSjekket: Int = 0
)
