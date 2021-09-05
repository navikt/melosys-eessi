package no.nav.melosys.eessi.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.websocket.server.PathParam;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.BucinfoDto;
import no.nav.melosys.eessi.controller.dto.SaksrelasjonDto;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.exception.ValidationException;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import no.nav.security.token.support.core.api.Protected;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Protected
@Slf4j
@RestController
@RequestMapping("/sak")
public class SakController {

    private final EuxService euxService;
    private final SaksrelasjonService saksrelasjonService;

    @Autowired
    public SakController(EuxService euxService, SaksrelasjonService saksrelasjonService) {
        this.euxService = euxService;
        this.saksrelasjonService = saksrelasjonService;
    }


    @GetMapping("/{gsakSaksnummer}/bucer")
    @ApiOperation(value = "Henter bucer tilknyttet en sak")
    public List<BucinfoDto> hentTilknyttedeBucer(@PathVariable Long gsakSaksnummer,
                                                 @RequestParam(required = false) List<String> statuser) {
        return saksrelasjonService.finnVedGsakSaksnummer(gsakSaksnummer).stream()
                .map(FagsakRinasakKobling::getRinaSaksnummer)
                .map(euxService::finnBUC)
                .flatMap(Optional::stream)
                .map(buc -> BucinfoDto.av(buc, statuser, euxService.hentRinaUrl(buc.getId())))
                .collect(Collectors.toList());
    }

    @PostMapping
    @ApiOperation("Lagrer en saksrelasjon mellom en rinasak og en gsak-sak")
    public ResponseEntity<Void> lagreSaksrelasjon(@RequestBody SaksrelasjonDto saksrelasjonDto)
            throws ValidationException {
        validerSaksrelasjonDto(saksrelasjonDto);

        saksrelasjonService.lagreKobling(saksrelasjonDto.getGsakSaksnummer(),
                saksrelasjonDto.getRinaSaksnummer(), BucType.valueOf(saksrelasjonDto.getBucType()));

        return ResponseEntity.ok().build();
    }

    @GetMapping
    @ApiOperation(value = "Søker etter saksrelasjon basert på enten rinaSaksnummer eller gsakSaksnummer")
    public List<SaksrelasjonDto> hentSaksrelasjon(
            @PathParam("rinaSaksnummer") String rinaSaksnummer,
            @PathParam("gsakSaksnummer") Long gsakSaksnummer) throws ValidationException {

        if (!StringUtils.hasText(rinaSaksnummer) && gsakSaksnummer != null) {
            return saksrelasjonService.finnVedGsakSaksnummer(gsakSaksnummer).stream()
                    .map(SaksrelasjonDto::av)
                    .collect(Collectors.toList());
        } else if (StringUtils.hasText(rinaSaksnummer) && gsakSaksnummer == null) {
            return saksrelasjonService.søkEtterSaksnummerFraRinaSaksnummer(rinaSaksnummer)
                    .map(saksnummer -> Collections.singletonList(new SaksrelasjonDto(saksnummer, rinaSaksnummer, null)))
                    .orElse(Collections.emptyList());
        }

        throw new ValidationException("Kun en av rinaSaksnummer og gsakSaksnummer kan spørres på");
    }

    private void validerSaksrelasjonDto(SaksrelasjonDto saksrelasjonDto) throws ValidationException {
        if (saksrelasjonDto.getGsakSaksnummer() == null || saksrelasjonDto.getGsakSaksnummer() < 1L) {
            throw new ValidationException("gsakSaksnummer kan ikke være tom");
        } else if (!StringUtils.hasText(saksrelasjonDto.getRinaSaksnummer())) {
            throw new ValidationException("rinaSaksnummer kan ikke være tom");
        }

        String rinaSaksnummer = saksrelasjonDto.getRinaSaksnummer();
        Optional<FagsakRinasakKobling> eksisterende = saksrelasjonService.finnVedRinaSaksnummer(rinaSaksnummer);

        if (eksisterende.isPresent() && !eksisterende.get().getGsakSaksnummer().equals(saksrelasjonDto.getGsakSaksnummer())) {
            throw new ValidationException("Rinasak " + saksrelasjonDto.getRinaSaksnummer() +
                    " er allerede koblet mot gsakSaksnummer " + eksisterende.get().getGsakSaksnummer());
        }
    }
}
