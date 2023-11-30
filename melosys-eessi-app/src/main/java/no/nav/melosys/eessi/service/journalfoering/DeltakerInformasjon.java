package no.nav.melosys.eessi.service.journalfoering;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeltakerInformasjon {
    private String id;
    private String name;

}
