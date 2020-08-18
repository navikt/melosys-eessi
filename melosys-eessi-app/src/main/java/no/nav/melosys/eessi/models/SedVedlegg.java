package no.nav.melosys.eessi.models;

import lombok.Value;

@Value
public class SedVedlegg {
    String filnavn;
    byte[] innhold;
}
