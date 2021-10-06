package no.nav.melosys.eessi.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;

public record SedMottattHendelseDto(Long id, SedHendelse sedHendelse, String journalpostId, boolean publisertKafka,
                                    @JsonSerialize(using = LocalDateTimeSerializer.class) LocalDateTime mottattDato,
                                    @JsonSerialize(using = LocalDateTimeSerializer.class)LocalDateTime sistEndretDato) {
}


