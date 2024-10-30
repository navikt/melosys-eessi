package no.nav.melosys.eessi.service.sed

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import no.nav.melosys.eessi.controller.dto.SedDataDto
import java.nio.file.Files
import java.nio.file.Paths

object SedDataStub {
    @JvmStatic
    fun getStub(): SedDataDto {
        val søknadURI = requireNotNull(SedDataStub::class.java.classLoader.getResource("mock/sedDataDtoStub.json")).toURI()
        val json = Files.readString(Paths.get(søknadURI))
        val objectMapper = ObjectMapper().apply {
            registerModule(JavaTimeModule())
        }
        return objectMapper.readValue(json, SedDataDto::class.java)
    }
}