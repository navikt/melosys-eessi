package no.nav.melosys.eessi.models

data class SedVedlegg(
    val tittel: String,
    val innhold: ByteArray
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SedVedlegg) return false

        if (tittel != other.tittel) return false
        if (!innhold.contentEquals(other.innhold)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tittel.hashCode()
        result = 31 * result + (innhold.let { it.contentHashCode() })
        return result
    }

    override fun toString(): String = "SedVedlegg(tittel=$tittel, innhold=${innhold.contentToString()})"
}
