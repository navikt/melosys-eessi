package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DnummerRekvisjonTilMellomlagring {

    private DnummerRekvisisjonKilde kilde = new DnummerRekvisisjonKilde();
    private DnummerRekvisisjonPersonopplysninger personopplysninger = new DnummerRekvisisjonPersonopplysninger();
    private DnummerRekvisisjonUtenlandskIdentifikasjon utenlandskIdentifikasjon = new DnummerRekvisisjonUtenlandskIdentifikasjon();
    private DnummerRekvisisjonBostedsadresse bostedsadresse = new DnummerRekvisisjonBostedsadresse();
    private DnummerRekvisisjonOppholdsadresse oppholdsadresse = new DnummerRekvisisjonOppholdsadresse();
    private DnummerRekvisisjonKontaktadresse kontaktadresse = new DnummerRekvisisjonKontaktadresse();
    private DnummerRekvisisjonDokumentasjon dokumentasjon = new DnummerRekvisisjonDokumentasjon();
    private List<DnummerRekvisisjonRelasjon> relasjoner = List.of();
    private DnummerRekvisisjonSivilstand sivilstand = new DnummerRekvisisjonSivilstand();

    public static class Builder {
        private final DnummerRekvisjonTilMellomlagring dnummerRekvisjonTilMellomlagring = new DnummerRekvisjonTilMellomlagring();

        public Builder medKilde(DnummerRekvisisjonKilde kilde) {
            dnummerRekvisjonTilMellomlagring.kilde = kilde;
            return this;
        }

        public Builder medPersonopplysninger(DnummerRekvisisjonPersonopplysninger personopplysninger) {
            dnummerRekvisjonTilMellomlagring.personopplysninger = personopplysninger;
            return this;
        }

        public Builder medUtenlandskIdentifikasjon(DnummerRekvisisjonUtenlandskIdentifikasjon utenlandskIdentifikasjon) {
            dnummerRekvisjonTilMellomlagring.utenlandskIdentifikasjon = utenlandskIdentifikasjon;
            return this;
        }

        public Builder medBostedsadresse(DnummerRekvisisjonBostedsadresse bostedsadresse) {
            dnummerRekvisjonTilMellomlagring.bostedsadresse = bostedsadresse;
            return this;
        }

        public Builder medOppholdsadresse(DnummerRekvisisjonOppholdsadresse oppholdsadresse) {
            dnummerRekvisjonTilMellomlagring.oppholdsadresse = oppholdsadresse;
            return this;
        }

        public Builder medKontaktadresse(DnummerRekvisisjonKontaktadresse kontaktadresse) {
            dnummerRekvisjonTilMellomlagring.kontaktadresse = kontaktadresse;
            return this;
        }

        public Builder medDokumentasjon(DnummerRekvisisjonDokumentasjon dokumentasjon) {
            dnummerRekvisjonTilMellomlagring.dokumentasjon = dokumentasjon;
            return this;
        }

        public Builder medRelasjoner(List<DnummerRekvisisjonRelasjon> relasjoner) {
            dnummerRekvisjonTilMellomlagring.relasjoner = relasjoner;
            return this;
        }

        public Builder medSivilstand(DnummerRekvisisjonSivilstand sivilstand) {
            dnummerRekvisjonTilMellomlagring.sivilstand = sivilstand;
            return this;
        }

        public DnummerRekvisjonTilMellomlagring build() {
            return dnummerRekvisjonTilMellomlagring;
        }
    }
}

