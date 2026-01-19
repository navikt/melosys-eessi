package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA011
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tools.jackson.databind.json.JsonMapper
import java.io.IOException

class A011MapperTest {

    private val a011Mapper = A011Mapper()
    private lateinit var a001: SED
    private val jsonMapper = JsonMapper.builder().build()

    @BeforeEach
    fun setup() {
        val inputStream = this::class.java.classLoader.getResourceAsStream("mock/sedA001.json")
            ?: throw IOException("File not found: mock/sedA001.json")
        a001 = jsonMapper.readValue(inputStream, SED::class.java)
    }

    @Test
    fun `map fra eksisterende SED A001 forvent korrekt SED A011`() {
        val a011 = a011Mapper.mapFraSed(a001)

        a011.shouldNotBeNull().run {
            sedType shouldBe SedType.A011.toString()

            nav.shouldNotBeNull()
                .bruker.shouldNotBeNull()
                .person.shouldNotBeNull()
                .fornavn shouldBe "Testfornavn1"

            medlemskap.shouldNotBeNull().shouldBeInstanceOf<MedlemskapA011>()
        }
    }
}
