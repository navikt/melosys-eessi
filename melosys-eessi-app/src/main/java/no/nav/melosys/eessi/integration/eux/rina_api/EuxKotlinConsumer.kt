package no.nav.melosys.eessi.integration.eux.rina_api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.melosys.eessi.integration.eux.rina_api.dto.v3.RinaSakOversiktV3
import no.nav.melosys.eessi.models.exception.IntegrationException
import org.springframework.http.HttpStatusCode
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import tools.jackson.databind.json.JsonMapper

@JsonIgnoreProperties(ignoreUnknown = true)
data class EuxApiErrorResponse(
    val status: String? = null,
    val messages: String? = null,
    val timestamp: String? = null
)

class EuxKotlinConsumer(
    private val euxRinaWebClient: WebClient,
    private val jsonMapper: JsonMapper = JsonMapper.builder().build(),
) {

    private fun parseErrorMessage(errorBody: String, statusCode: HttpStatusCode): String {
        return try {
            val errorResponse = jsonMapper.readValue(errorBody, EuxApiErrorResponse::class.java)
            errorResponse.messages ?: "Ukjent feil fra EUX Rina API"
        } catch (e: Exception) {
            // Fall tilbake til opprinnelig feilmelding hvis JSON-parsing feiler
            errorBody
        }
    }

    fun hentBucOversiktV3(rinaSaksnummer: String): RinaSakOversiktV3 {
        return euxRinaWebClient.get()
            .uri("/v3/buc/{rinaSaksnummer}/oversikt", rinaSaksnummer)
            .retrieve()
            .onStatus(HttpStatusCode::isError) { response ->
                response.bodyToMono(String::class.java)
                    .flatMap { errorBody ->
                        val errorMessage = parseErrorMessage(errorBody, response.statusCode())
                        Mono.error(
                            IntegrationException(
                                "Feil ved henting av BUC-oversikt V3 fra EUX Rina API. Status: ${response.statusCode()}, Feil: $errorMessage"
                            )
                        )
                    }
            }
            .bodyToMono(RinaSakOversiktV3::class.java)
            .block() ?: throw IntegrationException("Tomt svar fra EUX Rina API ved henting av BUC-oversikt V3")
    }

    fun resendSed(rinaSaksnummer: String, dokumentId: String) {
        val uri = "/cpi/resend/buc/{rinaSaksnummer}/sed/{dokumentId}"
        euxRinaWebClient.post()
            .uri(uri, rinaSaksnummer, dokumentId)
            .retrieve()
            .onStatus(HttpStatusCode::isError) { response ->
                response.bodyToMono(String::class.java)
                    .flatMap { errorBody ->
                        val errorMessage = parseErrorMessage(errorBody, response.statusCode())
                        val actualUri = uri.replace("{rinaSaksnummer}", rinaSaksnummer).replace("{dokumentId}", dokumentId)
                        Mono.error(
                            IntegrationException(
                                "Feil ved gjensending av SED fra EUX Rina API. " +
                                "URI: $actualUri, " +
                                "RinaSaksnummer: $rinaSaksnummer, " +
                                "DokumentId: $dokumentId, " +
                                "Status: ${response.statusCode()}, " +
                                "Feil: $errorMessage"
                            )
                        )
                    }
            }
            .bodyToMono(Void::class.java)
            .block()
    }


    fun resendSedListe(sedIds: List<String>) {
        val uri = "/cpi/resend/liste"
        val body = sedIds.joinToString("\n")

        euxRinaWebClient.post()
            .uri(uri)
            .bodyValue(body)
            .retrieve()
            .onStatus(HttpStatusCode::isError) { response ->
                response.bodyToMono(String::class.java)
                    .flatMap { errorBody ->
                        val errorMessage = parseErrorMessage(errorBody, response.statusCode())
                        Mono.error(
                            IntegrationException(
                                "Feil ved gjensending av SED-liste fra EUX Rina API. " +
                                "URI: $uri, " +
                                "Antall SEDer: ${sedIds.size}, " +
                                "Status: ${response.statusCode()}, " +
                                "Feil: $errorMessage"
                            )
                        )
                    }
            }
            .bodyToMono(Void::class.java)
            .block()
    }
}
