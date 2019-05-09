package no.nav.melosys.eessi.models;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity(name = "rinasak_kobling")
public class RinasakKobling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rina_id")
    private String rinaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "buc_type")
    private BucType bucType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fagsak_id", nullable = false)
    private FagsakKobling fagsakKobling;
}
