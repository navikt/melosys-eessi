package no.nav.melosys.eessi.models.vedlegg

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class SedMedVedlegg(
    val sed: BinaerFil? = null,
    val vedlegg: List<BinaerFil>? = emptyList()
) {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class BinaerFil(
        val filnavn: String,
        val mimeType: String?,
        val innhold: ByteArray
    ) {
        // Trenger normalt ikke å implementere equals og hashCode med data class men pga ByteArray så må vi det
        // Property with 'Array' type in a 'data' class: it is recommended to override 'equals()' and 'hashCode()
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is BinaerFil) return false

            if (filnavn != other.filnavn) return false
            if (mimeType != other.mimeType) return false
            if (!innhold.contentEquals(other.innhold)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = filnavn.hashCode()
            result = 31 * result + mimeType.hashCode()
            result = 31 * result + innhold.contentHashCode()
            return result
        }

        override fun toString(): String {
            return "BinaerFil(filnavn='$filnavn', mimeType='$mimeType', innhold=${innhold.contentToString()})"
        }
    }
}
