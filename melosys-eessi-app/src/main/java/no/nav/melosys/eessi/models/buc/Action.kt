// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.models.buc

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Action (
    var name: String? = null,
    var documentType: String? = null,
    var documentId: String? = null,
    var operation: String? = null,
)