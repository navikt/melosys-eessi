package no.nav.melosys.eessi.integration.pdl.dto.sed.adresse;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UtenlandskAdresse {
    String adressenavnNummer;
    String bygningEtasjeLeilighet;
    String bygning;
    String etasjenummer;
    String boenhet;
    String postboksNummerNavn;
    String postkode;
    String bySted;
    String regionDistriktOmraade;
    String distriktsnavn;
    String region;
    String landkode;
}
