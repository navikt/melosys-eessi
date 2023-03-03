package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ugyldiggjoere {
    private InvalideringSed sed;
}
