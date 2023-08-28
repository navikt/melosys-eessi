package no.nav.melosys.eessi.integration.pdl.dto.sed;

import java.util.List;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DnummerRekvisisjonPersonopplysninger {
    private String fornavn = "";
    private String etternavn = "";
    private String foedselsdato = "";
    private String kjoenn = "";
    private String foedested = "";
    private String foedeland = "";
    private List<String> statsborgerskap = Lists.newArrayList();

    public static class Builder {
        private DnummerRekvisisjonPersonopplysninger pdlSedPersonopplysninger = new DnummerRekvisisjonPersonopplysninger();

        public Builder medFornavn(String fornavn) {
            pdlSedPersonopplysninger.fornavn = fornavn;
            return this;
        }

        public Builder medEtternavn(String etternavn) {
            pdlSedPersonopplysninger.etternavn = etternavn;
            return this;
        }

        public Builder medFoedselsdato(String foedselsdato) {
            pdlSedPersonopplysninger.foedselsdato = foedselsdato;
            return this;
        }

        public Builder medKjoenn(String kjoenn) {
            pdlSedPersonopplysninger.kjoenn = kjoenn;
            return this;
        }

        public Builder medFoedested(String foedested) {
            pdlSedPersonopplysninger.foedested = foedested;
            return this;
        }

        public Builder medFoedeland(String foedeland) {
            pdlSedPersonopplysninger.foedeland = foedeland;
            return this;
        }

        public Builder medStatsborgerskap(List<String> statsborgerskap) {
            pdlSedPersonopplysninger.statsborgerskap = statsborgerskap;
            return this;
        }

        public DnummerRekvisisjonPersonopplysninger build() {
            return pdlSedPersonopplysninger;
        }
    }
}
