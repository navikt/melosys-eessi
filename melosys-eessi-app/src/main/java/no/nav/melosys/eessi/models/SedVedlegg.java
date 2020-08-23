package no.nav.melosys.eessi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SedVedlegg {
    private String tittel;
    private byte[] innhold;
}
