package no.nav.melosys.eessi.models;

import java.time.LocalDateTime;

import no.nav.melosys.eessi.kafka.consumers.SedHendelse;

// TODO: konverter til kotlin. Feiler nå selv om vi legger på @Record så venter med denne
public record SedMottattHendelseDto(
    Long id,
    SedHendelse sedHendelse,
    String journalpostId,
    boolean publisertKafka,
    LocalDateTime mottattDato,
    LocalDateTime sistEndretDato) {
}
