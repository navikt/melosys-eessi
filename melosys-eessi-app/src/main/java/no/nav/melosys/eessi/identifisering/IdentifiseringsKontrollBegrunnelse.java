package no.nav.melosys.eessi.identifisering;

public enum IdentifiseringsKontrollBegrunnelse {
    FØDSELSDATO("fødselsdato"),
    STATSBORGERSKAP("statsborgerskap"),
    KJØNN("kjønn"),
    UTENLANDSK_ID("utenlandsk-id"),
    EUX_RINA("Rina Integrasjon");

    private final String begrunnelse;

    IdentifiseringsKontrollBegrunnelse(String begrunnelse) {
        this.begrunnelse = begrunnelse;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }
}
