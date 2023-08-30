package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DnummerRekvisisjonTilMellomlagring {

    private DnummerRekvisisjonKilde kilde;
    private DnummerRekvisisjonPersonopplysninger personopplysninger;
    private DnummerRekvisisjonUtenlandskIdentifikasjon utenlandskIdentifikasjon;
    private DnummerRekvisisjonBostedsadresse bostedsadresse;
    private DnummerRekvisisjonOppholdsadresse oppholdsadresse;
    private DnummerRekvisisjonKontaktadresse kontaktadresse;
    private DnummerRekvisisjonDokumentasjon dokumentasjon;
    private List<DnummerRekvisisjonRelasjon> relasjoner;
    private DnummerRekvisisjonSivilstand sivilstand;
}

