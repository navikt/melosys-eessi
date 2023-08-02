package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PDLSed {
    private PDLSedKilde kilde = new PDLSedKilde();
    private PDLSedPersonopplysninger personopplysninger = new PDLSedPersonopplysninger();
    private PDLSedUtenlandskIdentifikasjon utenlandskIdentifikasjon = new PDLSedUtenlandskIdentifikasjon();
    private PDLSedBostedsadresse bostedsadresse = new PDLSedBostedsadresse();
    private PDLSedOppholdsadresse oppholdsadresse = new PDLSedOppholdsadresse();
    private PDLSedKontaktadresse kontaktadresse = new PDLSedKontaktadresse();
    private PDLSedDokumentasjon dokumentasjon = new PDLSedDokumentasjon();
    private List<PDLSedRelasjon> relasjoner = List.of();
    private PDLSedSivilstand sivilstand = new PDLSedSivilstand();

    public static class Builder {
        private PDLSed pdlSed = new PDLSed();

        public Builder medKilde(PDLSedKilde kilde) {
            pdlSed.kilde = kilde;
            return this;
        }

        public Builder medPersonopplysninger(PDLSedPersonopplysninger personopplysninger) {
            pdlSed.personopplysninger = personopplysninger;
            return this;
        }

        public Builder medUtenlandskIdentifikasjon(PDLSedUtenlandskIdentifikasjon utenlandskIdentifikasjon) {
            pdlSed.utenlandskIdentifikasjon = utenlandskIdentifikasjon;
            return this;
        }

        public Builder medBostedsadresse(PDLSedBostedsadresse bostedsadresse) {
            pdlSed.bostedsadresse = bostedsadresse;
            return this;
        }

        public Builder medOppholdsadresse(PDLSedOppholdsadresse oppholdsadresse) {
            pdlSed.oppholdsadresse = oppholdsadresse;
            return this;
        }

        public Builder medKontaktadresse(PDLSedKontaktadresse kontaktadresse) {
            pdlSed.kontaktadresse = kontaktadresse;
            return this;
        }

        public Builder medDokumentasjon(PDLSedDokumentasjon dokumentasjon) {
            pdlSed.dokumentasjon = dokumentasjon;
            return this;
        }

        public Builder medRelasjoner(List<PDLSedRelasjon> relasjoner) {
            pdlSed.relasjoner = relasjoner;
            return this;
        }

        public Builder medSivilstand(PDLSedSivilstand sivilstand) {
            pdlSed.sivilstand = sivilstand;
            return this;
        }

        public PDLSed build() {
            return pdlSed;
        }
    }
}

