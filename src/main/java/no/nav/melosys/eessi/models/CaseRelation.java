package no.nav.melosys.eessi.models;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity(name = "CASE_RELATION")
public class CaseRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rina_sakid")
    private String rinaId;

    @Column(name = "gsak_id")
    private Long gsakSaksnummer;
}
