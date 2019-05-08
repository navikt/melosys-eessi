package no.nav.melosys.eessi.kafka.producers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Periode {

    private String fom;
    private String tom;

}
