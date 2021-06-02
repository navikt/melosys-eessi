package no.nav.melosys.eessi.models.sed.nav;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import no.nav.melosys.eessi.integration.eux.rina_api.dto.Institusjon;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class X006FjernInstitusjon {

    private Institusjon institusjon;
}
