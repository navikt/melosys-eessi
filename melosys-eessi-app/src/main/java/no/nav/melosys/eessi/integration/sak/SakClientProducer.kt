package no.nav.melosys.eessi.integration.sak

import no.nav.melosys.eessi.security.GenericAuthFilterFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class SakClientProducer(@Value("\${melosys.integrations.gsak.sak-url}") private val url: String) {

    @Bean
    fun sakRestClient(
        webClientBuilder: WebClient.Builder,
        genericAuthFilterFactory: GenericAuthFilterFactory
    ): SakClient {
        return SakClient(
            webClientBuilder
                .baseUrl(url)
                .defaultHeaders { defaultHeaders(it) }
                .filter(genericAuthFilterFactory.getAzureFilter("sak"))
                .build()
        )
    }

    private fun defaultHeaders(httpHeaders: HttpHeaders) {
        httpHeaders.accept = listOf(MediaType.APPLICATION_JSON)
        httpHeaders.contentType = MediaType.APPLICATION_JSON
    }
}
