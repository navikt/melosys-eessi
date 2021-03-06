package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@JsonInclude(Include.NON_NULL)
@Data
public class Unntak {

    private String startdatoansattforsikret;

    private Grunnlag grunnlag;

    private SpesielleOmstendigheter spesielleomstendigheter;

    private String startdatokontraktansettelse;

    private String begrunnelse;

    private String a1grunnlag;
}
