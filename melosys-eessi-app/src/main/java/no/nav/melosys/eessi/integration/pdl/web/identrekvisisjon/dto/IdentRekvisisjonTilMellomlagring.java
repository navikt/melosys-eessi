package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentRekvisisjonTilMellomlagring {

    private IdentRekvisisjonKilde kilde;
    private IdentRekvisisjonPersonopplysninger personopplysninger;
    private IdentRekvisisjonUtenlandskIdentifikasjon utenlandskIdentifikasjon;
    private IdentRekvisisjonBostedsadresse bostedsadresse;
    private IdentRekvisisjonOppholdsadresse oppholdsadresse;
    private IdentRekvisisjonKontaktadresse kontaktadresse;
    private IdentRekvisisjonDokumentasjon dokumentasjon;
    private List<IdentRekvisisjonRelasjon> relasjoner;
    private IdentRekvisisjonSivilstand sivilstand;
}

