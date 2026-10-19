package no.nav.melosys.eessi.integration.sak

import mu.KotlinLogging
import no.nav.melosys.eessi.integration.RestConsumer
import no.nav.melosys.eessi.models.exception.IntegrationException
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.util.UUID

private val log = KotlinLogging.logger {}

class SakClient(private val webClient: WebClient) : RestConsumer {

    fun getSak(arkivsakId: String): Sak {
        log.info("hentsak: sakId: {}", arkivsakId)
        return webClient.get()
            .uri("/{arkivsakId}", arkivsakId)
            // Sak API avviser kall uten X-Correlation-ID
            .header(CORRELATION_ID_HEADER, UUID.randomUUID().toString())
            .retrieve()
            .onStatus({ it.isError }) { clientResponse ->
                clientResponse.bodyToMono(String::class.java)
                    .defaultIfEmpty("Ukjent feil")
                    .map { body -> IntegrationException("Feil i integrasjon mot sak: $body") }
            }
            .bodyToMono<Sak>()
            .block() ?: throw IntegrationException("Tomt svar fra sak ved henting av sakId: $arkivsakId")
    }

    companion object {
        const val CORRELATION_ID_HEADER = "X-Correlation-ID"
    }
}
