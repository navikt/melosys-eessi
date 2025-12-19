package no.nav.melosys.eessi.integration.saf.dto;

public enum Variantformat {
    ARKIV, // Arkivvarianten, det ferdige dokumentet. Er typisk PDF eller PDFA.
    ORIGINAL // Originalvarianten, er som regel metadata knyttet til opprettingen av dokumentet. Er typisk XML eller JSON.
}
