package no.nav.melosys.eessi.integration.eux.rina_api

import no.nav.melosys.eessi.integration.interceptor.CorrelationIdExchangeFilter
import no.nav.melosys.eessi.security.GenericAuthFilterFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import tools.jackson.databind.json.JsonMapper

@Configuration
class EuxKotlinConsumerProducer(
    private val webClientBuilder: WebClient.Builder,
    private val correlationIdExchangeFilter: CorrelationIdExchangeFilter,
    private val genericAuthFilterFactory: GenericAuthFilterFactory
) {

    @Bean
    fun euxKotlinConsumer(
        @Value("\${melosys.integrations.eux-rina-api-url}") euxRinaApiUrl: String,
        @Qualifier("euxJsonMapper") euxJsonMapper: JsonMapper
    ): EuxKotlinConsumer {
        val webClient = webClientBuilder
            .baseUrl(euxRinaApiUrl)
            .filter(correlationIdExchangeFilter)
            .filter(genericAuthFilterFactory.getAzureFilter("eux-rina-api"))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
        return EuxKotlinConsumer(webClient, euxJsonMapper)
    }
}
