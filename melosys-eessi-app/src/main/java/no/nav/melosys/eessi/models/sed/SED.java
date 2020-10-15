
package no.nav.melosys.eessi.models.sed;


import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import lombok.Data;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.models.sed.nav.*;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SED {

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "sed")
    @JsonTypeIdResolver(MedlemskapTypeResolver.class)
    private Medlemskap medlemskap;

    private Nav nav;

    @JsonProperty(value = "sed")
    private String sedType;

    private String sedGVer;

    private String sedVer;

    public Optional<Person> finnPerson() {
        return erXSED()
                ? Optional.ofNullable(nav.getSak()).map(Sak::getKontekst).map(Kontekst::getBruker).map(Bruker::getPerson)
                : Optional.of(nav.getBruker().getPerson());

    }

    public boolean erXSED() {
        return SedType.valueOf(sedType).erXSED();
    }
}
