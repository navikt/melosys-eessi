package no.nav.melosys.eessi.models;

import lombok.Value;

@Value
public class SedVedlegg {
    private final String filnavn;
    private final byte[] innhold;
}
