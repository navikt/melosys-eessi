package no.nav.melosys.eessi.models;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sed_hendelse")
    private SedHendelse sedHendelse;

    @Column(name = "journalpost_id")
    private String journalpostId;
}
