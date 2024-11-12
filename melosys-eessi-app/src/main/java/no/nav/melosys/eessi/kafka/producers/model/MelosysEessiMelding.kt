package no.nav.melosys.eessi.kafka.producers.model

data class MelosysEessiMelding(
    var sedId: String? = null,
    var sedVersjon: String? = null,
    var rinaSaksnummer: String? = null,
    var avsender: Avsender? = null,
    var journalpostId: String? = null,
    var dokumentId: String? = null,
    var gsakSaksnummer: Long? = null,
    var aktoerId: String? = null,
    var statsborgerskap: List<Statsborgerskap> = emptyList(),
    var arbeidssteder: List<Arbeidssted> = emptyList(),
    var arbeidsland: List<Arbeidsland> = emptyList(),
    var periode: Periode? = null,
    var lovvalgsland: String? = null,
    var artikkel: String? = null,
    var erEndring: Boolean = false,
    var midlertidigBestemmelse: Boolean = false,
    var x006NavErFjernet: Boolean = false,
    var ytterligereInformasjon: String? = null,
    var bucType: String? = null,
    var sedType: String? = null,
    var svarAnmodningUnntak: SvarAnmodningUnntak? = null,
    var anmodningUnntak: AnmodningUnntak? = null
)
