package no.nav.melosys.eessi.integration.sak

import mu.KotlinLogging
import no.nav.melosys.eessi.config.MDCOperations.X_CORRELATION_ID
import no.nav.melosys.eessi.config.MDCOperations.getCorrelationId
import no.nav.melosys.eessi.integration.RestConsumer
import no.nav.melosys.eessi.models.exception.IntegrationException
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

private val log = KotlinLogging.logger {}

class SakClient(private val webClient: WebClient) : RestConsumer {

    fun getSak(arkivsakId: String): Sak {
        val correlationID = getCorrelationId()
        log.info("hentsak: correlationId: {}, sakId: {}", correlationID, arkivsakId)
        return webClient.get()
            .uri("/{arkivsakId}", arkivsakId)
            .header(X_CORRELATION_ID, correlationID)
            .retrieve()
            .onStatus({ it.isError }) { clientResponse ->
                clientResponse.bodyToMono(String::class.java)
                    .defaultIfEmpty("Ukjent feil")
                    .map { body -> IntegrationException("Feil i integrasjon mot sak: $body") }
            }
            .bodyToMono<Sak>()
            .block() ?: throw IntegrationException("Tomt svar fra sak ved henting av sakId: $arkivsakId")
    }
}
