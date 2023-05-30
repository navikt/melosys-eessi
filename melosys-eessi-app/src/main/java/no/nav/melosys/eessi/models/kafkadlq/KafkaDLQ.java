package no.nav.melosys.eessi.models.kafkadlq;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ko_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "kafka_dlq")
public abstract class KafkaDLQ {

    @Id
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "ko_type", insertable = false, updatable = false)
    private KoType koType;

    @Column(name = "tid_registrert")
    private LocalDateTime tidRegistrert;

    @Column(name = "tid_sist_rekjort")
    private LocalDateTime tidSistRekjort;

    @Column(name="siste_feilmelding")
    private String sisteFeilmelding;

    @Column(name = "antall_rekjoringer")
    private int antallRekjoringer;

    public void økAntallRekjøringerMed1() {
        antallRekjoringer++;
    }

    public abstract String hentMeldingSomStreng() throws JsonProcessingException;
}


