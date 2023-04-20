package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@RequiredArgsConstructor
public class InvalideringSed {

    private String utstedelsesdato;
    private String type;
    private Grunn grunn;
}
