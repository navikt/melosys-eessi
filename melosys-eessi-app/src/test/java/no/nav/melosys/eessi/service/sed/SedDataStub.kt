package no.nav.melosys.eessi.service.sed

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.service.sed.mapper.til_sed.SedMapper
import java.nio.file.Files
import java.nio.file.Paths

object SedDataStub {
    @JvmStatic
    fun getStub(): SedDataDto = getStub("mock/sedDataDtoStub.json")

    fun getStub(fileName: String): SedDataDto {
        val søknadURI =
            SedDataStub::class.java.classLoader.getResource(fileName) ?: throw RuntimeException("Fant ikke filen $fileName")
        val json = Files.readString(Paths.get(søknadURI.toURI()))
        val objectMapper = jacksonObjectMapper().apply {
            registerModule(JavaTimeModule())
        }
        return objectMapper.readValue<SedDataDto>(json)
    }

    inline fun <reified T : SedMapper> mapTilSed(
        erCDM4_3: Boolean,
        testData: String,
        noinline block: SedDataDto.() -> Unit = {}
    ): SED = T::class.java.getDeclaredConstructor().newInstance().mapTilSed(getStub(testData).apply {
        block()
    }, erCDM4_3)
}
