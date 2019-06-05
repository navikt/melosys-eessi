package no.nav.melosys.eessi.models.sed


import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver


data class SED (
    @field:JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "sed")
    @field:JsonTypeIdResolver(MedlemskapTypeResolver::class)
    var medlemskap: Medlemskap? = null,
    var nav: Nav? = null,
    var sed: String? = null,
    var sedGVer: String? = null,
    var sedVer: String? = null
)