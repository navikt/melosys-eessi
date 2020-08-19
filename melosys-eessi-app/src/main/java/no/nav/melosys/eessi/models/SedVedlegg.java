package no.nav.melosys.eessi.models;

import lombok.Value;

@Value
public class SedVedlegg {
    private String filnavn;
    private byte[] innhold;
}
