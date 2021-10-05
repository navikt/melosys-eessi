package no.nav.melosys.eessi.models;

import java.time.LocalDateTime;

import no.nav.melosys.eessi.kafka.consumers.SedHendelse;

public record SedMottattHendelseDto(Long id, SedHendelse sedHendelse, String journalpostId, boolean publisertKafka,
                                    LocalDateTime mottattDato, LocalDateTime sistEndretDato) {
}


