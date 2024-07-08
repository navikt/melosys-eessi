package no.nav.melosys.eessi.models.person

import java.time.LocalDate

data class PersonModell(
    val ident: String?,
    val fornavn: String?,
    val etternavn: String?,
    val fødselsdato: LocalDate,
    val statsborgerskapLandkodeISO2: Collection<String>,
    val utenlandskId: Collection<UtenlandskId>,
    val erOpphørt: Boolean,
    val kjønn: Kjønn?
) {
    data class PersonModellBuilder(
        var ident: String? = null,
        var fornavn: String? = null,
        var etternavn: String? = null,
        var fødselsdato: LocalDate? = null,
        var statsborgerskapLandkodeISO2: Collection<String>? = null,
        var utenlandskId: Collection<UtenlandskId>? = null,
        var erOpphørt: Boolean = false,
        var kjønn: Kjønn? = null
    ) {
        fun ident(ident: String) = apply { this.ident = ident }
        fun fornavn(fornavn: String) = apply { this.fornavn = fornavn }
        fun etternavn(etternavn: String) = apply { this.etternavn = etternavn }
        fun fødselsdato(fødselsdato: LocalDate) = apply { this.fødselsdato = fødselsdato }
        fun statsborgerskapLandkodeISO2(statsborgerskapLandkodeISO2: Collection<String>) = apply { this.statsborgerskapLandkodeISO2 = statsborgerskapLandkodeISO2 }
        fun utenlandskId(utenlandskId: Collection<UtenlandskId>) = apply { this.utenlandskId = utenlandskId }
        fun erOpphørt(erOpphørt: Boolean) = apply { this.erOpphørt = erOpphørt }
        fun kjønn(kjønn: Kjønn) = apply { this.kjønn = kjønn }

        fun build(): PersonModell {
            return PersonModell(
                ident,
                fornavn,
                etternavn,
                fødselsdato ?: throw IllegalStateException("Fødselsdato er påkrevd"),
                statsborgerskapLandkodeISO2 ?: emptyList(),
                utenlandskId ?: emptyList(),
                erOpphørt,
                kjønn
            )
        }
    }

    companion object {
        @JvmStatic
        fun builder() = PersonModellBuilder()
    }
}
