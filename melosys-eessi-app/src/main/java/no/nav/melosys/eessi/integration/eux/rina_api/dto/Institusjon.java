
package no.nav.melosys.eessi.integration.eux.rina_api.dto;

import java.util.List;
import lombok.Data;

@Data
public class Institusjon {

    private String akronym;
    private String id;
    private String landkode;
    private String navn;
    private List<TilegnetBuc> tilegnetBucs;
}
