// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.adresse;

import java.util.List;

public class PostadresseIFrittFormat {
    List<String> adresselinje;
    Poststed poststed;

    @java.lang.SuppressWarnings("all")
    public List<String> getAdresselinje() {
        return this.adresselinje;
    }

    @java.lang.SuppressWarnings("all")
    public Poststed getPoststed() {
        return this.poststed;
    }

    @java.lang.SuppressWarnings("all")
    public PostadresseIFrittFormat(final List<String> adresselinje, final Poststed poststed) {
        this.adresselinje = adresselinje;
        this.poststed = poststed;
    }
}
