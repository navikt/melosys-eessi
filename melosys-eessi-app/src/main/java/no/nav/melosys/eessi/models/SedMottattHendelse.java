package no.nav.melosys.eessi.models;

import java.time.LocalDateTime;
import javax.persistence.*;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.boot.actuate.audit.listener.AuditListener;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity(name = "sed_mottatt_hendelse")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@EntityListeners(AuditingEntityListener.class)
public class SedMottattHendelse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Type(type = "jsonb")
    @Column(name = "sed_hendelse")
    private SedHendelse sedHendelse;

    @Column(name = "journalpost_id")
    private String journalpostId;

    @Column(name = "publisert_kafka")
    private boolean publisertKafka;

    @CreatedDate
    @Column(name = "mottatt_dato")
    private LocalDateTime mottattDato;

    @LastModifiedDate
    @Column(name = "endret_dato")
    private LocalDateTime sistEndretDato;

}
