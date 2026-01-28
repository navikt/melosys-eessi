package no.nav.melosys.eessi.controller.dto

import com.fasterxml.jackson.annotation.JsonValue

/**
 * Formål for A008 SED (CDM 4.4).
 * Verdiene tilsvarer RINA-verdiene som sendes i SED.
 */
enum class A008Formaal(@JsonValue val rinaVerdi: String) {
    ENDRINGSMELDING("endringsmelding"),
    ARBEID_FLERE_LAND("arbeid_flere_land");
}
