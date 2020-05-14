package no.nav.melosys.eessi.models;

import lombok.Value;

@Value
public class Vedlegg {
    private final String filnavn;
    private final byte[] innhold;
}
