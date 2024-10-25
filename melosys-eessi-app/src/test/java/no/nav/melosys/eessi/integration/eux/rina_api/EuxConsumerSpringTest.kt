package no.nav.melosys.eessi.integration.eux.rina_api

import io.kotest.matchers.shouldBe
import no.nav.melosys.eessi.models.buc.BUC
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(
    classes = [
        EuxConsumerSpringTest.Config::class,
        EuxConsumer::class,
        RestTemplateAutoConfiguration::class,
        JacksonAutoConfiguration::class,
    ]
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EuxConsumerSpringTest(
    @Autowired private val euxConsumer: EuxConsumer,
) {
    companion object {
        private var mockServer = MockWebServer().apply {
            start()
        }
    }

    @TestConfiguration
    class Config {
        @Bean
        fun restTemplateTest(builder: RestTemplateBuilder): RestTemplate {
            return builder
                .rootUri("http://localhost:${mockServer.port}")
                .build()
        }
    }

    @AfterAll
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun `skal håntere tom json eller null verdier ved henting av BUC`() {
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("{}")
                .addHeader("Content-Type", "application/json")
        )

        val response: BUC = euxConsumer.hentBUC("1234")

        response shouldBe BUC(
            actions = listOf(),
            participants = listOf(),
            documents = listOf(),
        )
    }

    @Test
    fun `skal håntere null verdier ved henting av BUC`() {
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """{
                    "actions": null,
                    "documents": null,
                    "participants": null
            }""""
                )
                .addHeader("Content-Type", "application/json")
        )

        val response: BUC = euxConsumer.hentBUC("1234")

        response shouldBe BUC(
            actions = listOf(),
            participants = listOf(),
            documents = listOf(),
        )
    }

}
