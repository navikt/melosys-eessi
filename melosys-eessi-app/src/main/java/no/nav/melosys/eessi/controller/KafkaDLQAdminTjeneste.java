package no.nav.melosys.eessi.controller;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.KafkaDLQDto;
import no.nav.melosys.eessi.models.kafkadlq.KafkaDLQ;
import no.nav.melosys.eessi.service.kafkadlq.KafkaDLQService;
import no.nav.security.token.support.core.api.Unprotected;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/kafka/dlq")
@Profile("!local-q2")
public class KafkaDLQAdminTjeneste {

    private static final String API_KEY_HEADER = "X-MELOSYS-ADMIN-APIKEY";

    private final KafkaDLQService kafkaDLQService;

    @Value("${melosys.admin.api-key}")
    private String apiKey;

    @GetMapping()
    public ResponseEntity<List<KafkaDLQDto>> hentFeiledeMeldinger(@RequestHeader(API_KEY_HEADER) String apiKey) {
        validerApikey(apiKey);

        return ResponseEntity.ok(kafkaDLQService.hentFeiledeKafkaMeldinger()
            .stream()
            .map(this::mapEntitetTilDto)
            .toList());
    }

    @PostMapping("/{uuid}/restart")
    public ResponseEntity<Void> rekjørKafkaMelding(@PathVariable String uuid, @RequestHeader(API_KEY_HEADER) String apiKey) {
        validerApikey(apiKey);

        kafkaDLQService.rekjørKafkaMelding(UUID.fromString(uuid));

        return ResponseEntity.ok().build();
    }

    @PostMapping("/restart/alle")
    public ResponseEntity<Void> rekjørAlleKafkaMelding(@RequestHeader(API_KEY_HEADER) String apiKey) {
        validerApikey(apiKey);

        kafkaDLQService.rekjørAlleKafkaMeldinger();

        return ResponseEntity.ok().build();
    }


    @SneakyThrows
    private KafkaDLQDto mapEntitetTilDto(KafkaDLQ entitet) {
        return KafkaDLQDto.builder()
            .id(entitet.getId().toString())
            .queueType(entitet.getQueueType())
            .sisteFeilmelding(entitet.getSisteFeilmelding())
            .tidRegistrert(entitet.getTidRegistrert())
            .tidSistRekjort(entitet.getTidSistRekjort())
            .antallRekjoringer(entitet.getAntallRekjoringer())
            .melding(entitet.hentMeldingSomStreng())
            .skip(entitet.getSkip())
            .build();

    }

    private void validerApikey(String value) {
        if (!apiKey.equals(value)) {
            throw new SecurityException("Ugyldig API-nøkkel");
        }
    }
}
