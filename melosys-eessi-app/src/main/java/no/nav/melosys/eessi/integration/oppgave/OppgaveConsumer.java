// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.integration.oppgave;

import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.integration.RestUtils;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static no.nav.melosys.eessi.config.MDCOperations.X_CORRELATION_ID;
import static no.nav.melosys.eessi.config.MDCOperations.getCorrelationId;

public class OppgaveConsumer implements RestConsumer {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OppgaveConsumer.class);
    private final WebClient webClient;

    public OppgaveConsumer(WebClient webClient) {
        this.webClient = webClient;
    }

    public HentOppgaveDto hentOppgave(String oppgaveID) {
        var correlationID = getCorrelationId();
        log.debug("hentOppgave, id: {}, correlationID: {}", oppgaveID, correlationID);
        return webClient.get().uri("/oppgaver/{oppgaveID}", oppgaveID).header(X_CORRELATION_ID, correlationID).retrieve().onStatus(HttpStatusCode::isError, this::håndterFeil).bodyToMono(HentOppgaveDto.class).block();
    }

    public HentOppgaveDto opprettOppgave(OppgaveDto oppgaveDto) {
        var correlationID = getCorrelationId();
        log.info("opprettOppgave, correlationID: {}", correlationID);
        return webClient.post().uri("/oppgaver").header(X_CORRELATION_ID, correlationID).bodyValue(oppgaveDto).retrieve().onStatus(HttpStatusCode::isError, this::håndterFeil).bodyToMono(HentOppgaveDto.class).block();
    }

    public HentOppgaveDto oppdaterOppgave(String oppgaveID, OppgaveOppdateringDto oppgaveOppdateringDto) {
        var correlationID = getCorrelationId();
        log.info("oppdaterOppgave, id: {}, correlationID: {}", oppgaveID, correlationID);
        return webClient.patch().uri("/oppgaver/{oppgaveID}", oppgaveID).header(X_CORRELATION_ID, correlationID).bodyValue(oppgaveOppdateringDto).retrieve().onStatus(HttpStatusCode::isError, this::håndterFeil).bodyToMono(HentOppgaveDto.class).block();
    }

    private Mono<? extends Throwable> håndterFeil(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class).map(RestUtils::hentFeilmeldingForOppgave).map(IntegrationException::new);
    }
}
