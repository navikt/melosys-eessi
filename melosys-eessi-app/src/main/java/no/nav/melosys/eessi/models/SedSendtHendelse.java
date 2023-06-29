package no.nav.melosys.eessi.models;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity(name = "sed_sendt_hendelse")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SedSendtHendelse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "sed_id")
    private String sedId;

    @Column(name = "rina_sak_id")
    private String rinaSakId;

    @Column(name = "journalfoert")
    private boolean journalfoert;
}
