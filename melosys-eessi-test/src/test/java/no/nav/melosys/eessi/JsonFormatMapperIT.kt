package no.nav.melosys.eessi

import io.kotest.matchers.types.shouldBeInstanceOf
import jakarta.persistence.EntityManagerFactory
import no.nav.melosys.eessi.config.HibernateJackson3FormatMapper
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

/**
 * Hibernate må bruke [HibernateJackson3FormatMapper] og ikke sin egen Jackson 2-baserte
 * JacksonJsonFormatMapper. Ellers feiler lagring av SED med polymorf medlemskap:
 *
 *     InvalidTypeIdException: Could not resolve type id 'A003' as a subtype of Medlemskap
 *
 * Mapperen konfigureres via `spring.jpa.properties.hibernate.type.json_format_mapper` i
 * application.yml. Den blir kun plukket opp så lenge EntityManagerFactory kommer fra Spring Boot
 * sin autokonfigurasjon. Definerer noen en egen LocalContainerEntityManagerFactoryBean (slik
 * DatabaseConfig gjorde i nais-profilen), forkastes hele spring.jpa.properties-kartet i stillhet,
 * og Hibernate faller tilbake på Jackson 2.
 *
 * Denne testen fanger nettopp det.
 */
class JsonFormatMapperIT : ComponentTestBaseKotlin() {

    @Autowired
    lateinit var entityManagerFactory: EntityManagerFactory

    @Test
    fun `hibernate skal bruke Jackson 3 som JSON format mapper`() {
        entityManagerFactory.unwrap(SessionFactory::class.java)
            .sessionFactoryOptions
            .jsonFormatMapper
            .shouldBeInstanceOf<HibernateJackson3FormatMapper>()
    }
}
