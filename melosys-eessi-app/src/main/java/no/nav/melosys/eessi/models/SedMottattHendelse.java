package no.nav.melosys.eessi.models;

import java.time.LocalDateTime;
import jakarta.persistence.*;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.*;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity(name = "sed_mottatt_hendelse")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Convert(attributeName = "jsonb", converter = JsonBinaryType.class)
@EntityListeners(AuditingEntityListener.class)
public class SedMottattHendelse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
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
