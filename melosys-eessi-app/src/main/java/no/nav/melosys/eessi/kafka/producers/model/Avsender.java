package no.nav.melosys.eessi.kafka.producers.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Avsender {
    private final String avsenderID;
    private final String landkode;
}
