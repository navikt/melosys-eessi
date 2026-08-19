package no.nav.melosys.eessi.config

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003
import no.nav.melosys.eessi.models.sed.nav.Bruker
import no.nav.melosys.eessi.models.sed.nav.Nav
import no.nav.melosys.eessi.models.sed.nav.Person
import no.nav.melosys.eessi.models.sed.nav.Statsborgerskap
import no.nav.melosys.eessi.models.sed.nav.VedtakA003
import org.hibernate.cfg.AvailableSettings
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.type.descriptor.WrapperOptions
import org.hibernate.type.descriptor.java.spi.JavaTypeBasicAdaptor
import org.hibernate.type.format.FormatMapper
import org.junit.jupiter.api.Test
import org.springframework.jdbc.datasource.SimpleDriverDataSource
import javax.sql.DataSource

/**
 * [DatabaseConfig] er kun aktiv i `nais`-profilen og bygger sin egen
 * LocalContainerEntityManagerFactoryBean. Da slår Spring Boot sin JPA-autokonfigurasjon seg av, og
 * `spring.jpa.properties.*` fra application.yml blir IKKE plukket opp.
 *
 * Konsekvensen var at Hibernate i prod falt tilbake på sin innebygde Jackson 2-baserte
 * JacksonJsonFormatMapper i stedet for [HibernateJackson3FormatMapper]. Jackson 2 kjenner ikke igjen
 * `tools.jackson.databind.annotation.JsonTypeIdResolver` på SED.medlemskap, og forsøker derfor å
 * tolke type-id-en "A003" som et klassenavn:
 *
 *     InvalidTypeIdException: Could not resolve type id 'A003' as a subtype of Medlemskap
 *
 * Hibernate gjør en deepCopy (= toString + fromString) ved persist, så feilen traff
 * `SedLagerService.lagreSedSeparatTransaksjon` og ga i prod:
 * "Kunne ikke lagre SED ... i sed mottatt lager for tredjelandsborger uten arbeidssted i Norge".
 *
 * Komponenttestene fanget ikke opp dette fordi de kjører uten `nais`-profilen, og dermed får
 * EntityManagerFactory fra Spring Boot sin autokonfigurasjon (som leser application.yml).
 */
class DatabaseConfigTest {

    private val databaseConfig = object : DatabaseConfig() {
        override fun userDataSource(): DataSource = SimpleDriverDataSource()
    }

    private fun konfigurertJsonFormatMapper(): FormatMapper =
        databaseConfig.entityManagerFactory()
            .jpaPropertyMap[AvailableSettings.JSON_FORMAT_MAPPER]
            .shouldBeInstanceOf<FormatMapper>()

    @Test
    fun `nais-profilen skal konfigurere Jackson 3 som Hibernate sin JSON format mapper`() {
        konfigurertJsonFormatMapper().shouldBeInstanceOf<HibernateJackson3FormatMapper>()
    }

    @Test
    fun `konfigurert format mapper skal tale deepCopy av A003-SED med polymorf medlemskap`() {
        val sed = SED(
            sedType = SedType.A003.name,
            medlemskap = MedlemskapA003(vedtak = VedtakA003(land = "DE")),
            nav = Nav(
                bruker = Bruker(
                    person = Person(
                        foedselsdato = "1990-01-01",
                        fornavn = "Test",
                        etternavn = "Person",
                        statsborgerskap = listOf(Statsborgerskap(land = "US"))
                    )
                ),
                arbeidssted = emptyList(),
                arbeidsland = emptyList()
            )
        )

        deepCopy(konfigurertJsonFormatMapper(), sed) shouldBe sed
    }

    /** Tilsvarer det Hibernate gjør i FormatMapperBasedJavaType.deepCopy ved persist. */
    private fun deepCopy(formatMapper: FormatMapper, sed: SED): SED {
        val javaType = JavaTypeBasicAdaptor(SED::class.java)
        val json = formatMapper.toString(sed, javaType, UbruktWrapperOptions)
        return formatMapper.fromString(json, javaType, UbruktWrapperOptions)
    }

    /** [HibernateJackson3FormatMapper] bruker ikke WrapperOptions, men signaturen krever en verdi. */
    private object UbruktWrapperOptions : WrapperOptions {
        override fun getSession(): SharedSessionContractImplementor? = null
        override fun getJsonFormatMapper(): FormatMapper = throw UnsupportedOperationException()
        override fun getXmlFormatMapper(): FormatMapper = throw UnsupportedOperationException()
    }
}
