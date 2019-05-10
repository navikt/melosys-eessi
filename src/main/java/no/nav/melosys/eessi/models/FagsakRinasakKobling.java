package no.nav.melosys.eessi.models;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity(name = "FAGSAK_RINASAK_KOBLING")
public class FagsakRinasakKobling {

    @Id
    @Column(name = "rina_saksnummer")
    private String rinaSaksnummer;

    @Column(name = "gsak_saksnummer")
    private Long gsakSaksnummer;

    @Enumerated(EnumType.STRING)
    @Column(name = "buc_type")
    private BucType bucType;

}
