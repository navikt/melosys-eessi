package no.nav.melosys.eessi.models.kafkadlq;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "queue_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "kafka_dlq")
public abstract class KafkaDLQ {

    @Id
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "queue_type", insertable = false, updatable = false)
    private QueueType queueType;

    @Column(name = "tid_registrert")
    private LocalDateTime tidRegistrert;

    @Column(name = "tid_sist_rekjort")
    private LocalDateTime tidSistRekjort;

    @Column(name = "siste_feilmelding")
    private String sisteFeilmelding;

    @Column(name = "antall_rekjoringer")
    private int antallRekjoringer;

    @Column(name = "skip")
    private Boolean skip = false;

    public void økAntallRekjøringerMed1() {
        antallRekjoringer++;
    }

    public abstract String hentMeldingSomStreng() throws JsonProcessingException;
}


