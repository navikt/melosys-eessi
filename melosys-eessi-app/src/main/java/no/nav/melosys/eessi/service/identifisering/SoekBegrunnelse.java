package no.nav.melosys.eessi.service.identifisering;

public enum SoekBegrunnelse {
    IDENTIFISERT,
    INGEN_TREFF,
    FLERE_TREFF,
    FEIL_FOEDSELSDATO,
    FEIL_STATSBORGERSKAP,
    PERSON_OPPHORT,
    FNR_IKKE_FUNNET
}