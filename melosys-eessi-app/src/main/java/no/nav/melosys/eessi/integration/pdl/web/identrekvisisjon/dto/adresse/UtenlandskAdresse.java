package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.adresse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Getter
@AllArgsConstructor
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
