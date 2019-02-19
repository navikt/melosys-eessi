package no.nav.melosys.eessi.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "CASE_RELATION")
public class CaseRelation {

    @Id
    private Long id;

    @Column(name = "rina_sakid")
    private String rinaId;

    @Column(name = "gsak_id")
    private Long gsakId;
}
