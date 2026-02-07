package no.nav.melosys.eessi.service.sed

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Test
import org.springframework.mock.env.MockEnvironment
import tools.jackson.databind.json.JsonMapper
import java.nio.file.Files
import java.nio.file.Paths

class JsonFieldMaskerTest {

    private val mapper = JsonMapper.builder().build()
    private val jsonFieldMasker = JsonFieldMasker(mapper, MockEnvironment())

    @Test
    fun `alt som ikke er i whitelist eller er boolean, float eller null skal maskeres`() {
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
                    "number": 456,
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
                    "list" : [ 1, 1, 1 ],
                    "list2" : [ "x", "x", "x" ],
                    "number" : 123,
                    "m2" : [ {
                      "a" : "xxx",
                      "b" : 1.2
                    } ]
                }
            }
        """
    }

    @Test
    fun `skal maskere felter i sedDataDto`() {
        val sedDataDto = SedDataStub.getStub("mock/sedDataDtoStub.json")
        val expectedJson = readFile("mock/sedDataDtoStubMasked.json")

        val maskedJson = jsonFieldMasker.toMaskedJson(sedDataDto)

        maskedJson shouldEqualJson expectedJson
    }

    @Test
    fun `prod-gcp maskerer persondata`() {
        val masker = JsonFieldMasker(mapper, MockEnvironment(), naisClusterName = "prod-gcp")
        val sedDataDto = SedDataStub.getStub("mock/sedDataDtoStub.json")

        masker.toMaskedJson(sedDataDto) shouldNotContain "MrFornavn"
    }

    @Test
    fun `dev-gcp viser full JSON uten maskering`() {
        val masker = JsonFieldMasker(mapper, MockEnvironment(), naisClusterName = "dev-gcp")
        val sedDataDto = SedDataStub.getStub("mock/sedDataDtoStub.json")

        masker.toMaskedJson(sedDataDto) shouldContain "MrFornavn"
    }

    @Test
    fun `local-mock profil viser full JSON uten maskering`() {
        val env = MockEnvironment().apply { setActiveProfiles("local-mock") }
        val masker = JsonFieldMasker(mapper, env)
        val sedDataDto = SedDataStub.getStub("mock/sedDataDtoStub.json")

        masker.toMaskedJson(sedDataDto) shouldContain "MrFornavn"
    }

    @Test
    fun `ukjent miljo maskerer persondata`() {
        val masker = JsonFieldMasker(mapper, MockEnvironment(), naisClusterName = "ukjent")
        val sedDataDto = SedDataStub.getStub("mock/sedDataDtoStub.json")

        masker.toMaskedJson(sedDataDto) shouldNotContain "MrFornavn"
    }

    private fun readFile(fileName: String): String {
        val url = (JsonFieldMaskerTest::class.java.classLoader.getResource(fileName)
            ?: throw RuntimeException("Fant ikke filen $fileName"))
        return Files.readString(Paths.get(url.toURI()))
    }
}
