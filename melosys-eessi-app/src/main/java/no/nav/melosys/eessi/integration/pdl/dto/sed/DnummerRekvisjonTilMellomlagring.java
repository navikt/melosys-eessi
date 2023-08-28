package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DnummerRekvisjonTilMellomlagring {

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
        private DnummerRekvisjonTilMellomlagring dnummerRekvisjonTilMellomlagring = new DnummerRekvisjonTilMellomlagring();

        public Builder medKilde(PDLSedKilde kilde) {
            dnummerRekvisjonTilMellomlagring.kilde = kilde;
            return this;
        }

        public Builder medPersonopplysninger(PDLSedPersonopplysninger personopplysninger) {
            dnummerRekvisjonTilMellomlagring.personopplysninger = personopplysninger;
            return this;
        }

        public Builder medUtenlandskIdentifikasjon(PDLSedUtenlandskIdentifikasjon utenlandskIdentifikasjon) {
            dnummerRekvisjonTilMellomlagring.utenlandskIdentifikasjon = utenlandskIdentifikasjon;
            return this;
        }

        public Builder medBostedsadresse(PDLSedBostedsadresse bostedsadresse) {
            dnummerRekvisjonTilMellomlagring.bostedsadresse = bostedsadresse;
            return this;
        }

        public Builder medOppholdsadresse(PDLSedOppholdsadresse oppholdsadresse) {
            dnummerRekvisjonTilMellomlagring.oppholdsadresse = oppholdsadresse;
            return this;
        }

        public Builder medKontaktadresse(PDLSedKontaktadresse kontaktadresse) {
            dnummerRekvisjonTilMellomlagring.kontaktadresse = kontaktadresse;
            return this;
        }

        public Builder medDokumentasjon(PDLSedDokumentasjon dokumentasjon) {
            dnummerRekvisjonTilMellomlagring.dokumentasjon = dokumentasjon;
            return this;
        }

        public Builder medRelasjoner(List<PDLSedRelasjon> relasjoner) {
            dnummerRekvisjonTilMellomlagring.relasjoner = relasjoner;
            return this;
        }

        public Builder medSivilstand(PDLSedSivilstand sivilstand) {
            dnummerRekvisjonTilMellomlagring.sivilstand = sivilstand;
            return this;
        }

        public DnummerRekvisjonTilMellomlagring build() {
            return dnummerRekvisjonTilMellomlagring;
        }
    }
}

