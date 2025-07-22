package no.nav.melosys.eessi.identifisering;

enum SoekBegrunnelse {
    IDENTIFISERT,
    INGEN_TREFF,
    FLERE_TREFF,
    FEIL_FOEDSELSDATO,
    FEIL_STATSBORGERSKAP,
    FEIL_NAVN,
    PERSON_OPPHORT,
    FNR_IKKE_FUNNET,
    INGEN_PERSON_I_SED
}
