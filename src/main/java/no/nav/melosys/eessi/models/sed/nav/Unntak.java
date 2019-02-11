package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@SuppressWarnings("unused")
@JsonInclude(Include.NON_NULL)
@Data
public class Unntak {

    private String startdatoansattforsikret; // TODO: Date?

    private Grunnlag grunnlag;

    private SpesielleOmstendigheter spesielleomstendigheter;

    private String startdatokontraktansettelse; // TODO: Date?

    private String begrunnelse;

    private String a1grunnlag; // TODO: enum?
}
