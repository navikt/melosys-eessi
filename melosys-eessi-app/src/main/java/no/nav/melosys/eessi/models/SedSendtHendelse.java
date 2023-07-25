package no.nav.melosys.eessi.models;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import org.hibernate.annotations.Type;
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

    @Type(type = "jsonb")
    @Column(name = "sed_hendelse")
    private SedHendelse sedHendelse;

    @Column(name = "journalpost_id")
    private String journalpostId;
}
