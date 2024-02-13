package no.nav.melosys.eessi.controller.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.melosys.eessi.models.kafkadlq.QueueType;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class KafkaDLQDto {

    private String id;
    private QueueType queueType;
    private LocalDateTime tidRegistrert;
    private LocalDateTime tidSistRekjort;
    private String sisteFeilmelding;
    private int antallRekjoringer;
    private String melding;
    private Boolean skip;
}
