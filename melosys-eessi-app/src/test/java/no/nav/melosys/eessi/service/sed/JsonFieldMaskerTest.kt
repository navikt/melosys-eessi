package no.nav.melosys.eessi.service.sed

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.assertions.json.shouldEqualJson
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

class JsonFieldMaskerTest {

    private val jsonFieldMasker = JsonFieldMasker(jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
    })

    @Test
    fun test() {
        val json = """
             {
                 "key1": "value1",
                 "key2": "value2",
                 "key3": "value3",
                 "map": {
                    "a": true,
                    "b": false,
                    "c": null,
                    "list": [1, 2, 3],
                    "list2": ["1", "2", "3"],
                    "number": 123,
                    "m2": [{
                        "a": "lla",
                        "b": 1.2
                        }
                    ]
                 }
             }
         """

        val sanitizeJson = jsonFieldMasker.sanitizeJson(json, setOf("key2"))

        sanitizeJson shouldEqualJson """
            {
                "key1" : "xxxxxx",
                "key2" : "value2",
                "key3" : "xxxxxx",
                "map" : {
                    "a" : true,
                    "b" : false,
                    "c" : null,
                    "list" : [ "x", "x", "x" ],
                    "list2" : [ "x", "x", "x" ],
                    "number" : "xxx",
                    "m2" : [ {
                      "a" : "xxx",
                      "b" : 1.2
                    } ]
                }
            }
        """
    }

    @Test
    fun testSedDataDto() {
        val sedDataDto = SedDataStub.getStub("mock/sedDataDtoStub.json")
        val expectedJson = readFile("mock/sedDataDtoStubMasked.json")

        val maskedJson = jsonFieldMasker.toMaskedJson(sedDataDto)

        maskedJson shouldEqualJson expectedJson
    }

    private fun readFile(fileName: String): String {
        val url = (JsonFieldMaskerTest::class.java.classLoader.getResource(fileName)
            ?: throw RuntimeException("Fant ikke filen $fileName"))
        return Files.readString(Paths.get(url.toURI()))
    }
}
