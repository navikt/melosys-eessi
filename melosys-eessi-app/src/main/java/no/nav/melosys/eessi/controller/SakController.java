package no.nav.melosys.eessi.controller;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.websocket.server.PathParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.BucinfoDto;
import no.nav.melosys.eessi.controller.dto.SaksrelasjonDto;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.ValidationException;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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
                .map(this::hentBuc)
                .filter(Objects::nonNull)
                .map(buc -> BucinfoDto.av(buc, statuser, euxService.hentRinaUrlPrefix()))
                .collect(Collectors.toList());
    }

    @PostMapping
    @ApiOperation("Lagrer en saksrelasjon mellom en rinasak og en gsak-sak")
    public ResponseEntity lagreSaksrelasjon(@RequestBody SaksrelasjonDto saksrelasjonDto)
            throws ValidationException, IntegrationException {
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

        if (StringUtils.isEmpty(rinaSaksnummer) && gsakSaksnummer != null) {
            return saksrelasjonService.finnVedGsakSaksnummer(gsakSaksnummer).stream()
                    .map(SaksrelasjonDto::av)
                    .collect(Collectors.toList());
        } else if (!StringUtils.isEmpty(rinaSaksnummer) && gsakSaksnummer == null) {
            Optional<FagsakRinasakKobling> fagsakRinasakKobling = saksrelasjonService.finnVedRinaSaksnummer(rinaSaksnummer);
            return fagsakRinasakKobling
                    .map(rinasakKobling -> Collections.singletonList(SaksrelasjonDto.av(rinasakKobling)))
                    .orElse(Collections.emptyList());
        }

        throw new ValidationException("Kun en av rinaSaksnummer og gsakSaksnummer kan spørres på");
    }

    private void validerSaksrelasjonDto(SaksrelasjonDto saksrelasjonDto) throws ValidationException {
        if (saksrelasjonDto.getGsakSaksnummer() == null || saksrelasjonDto.getGsakSaksnummer() < 1L) {
            throw new ValidationException("gsakSaksnummer kan ikke være tom");
        } else if (StringUtils.isEmpty(saksrelasjonDto.getRinaSaksnummer())) {
            throw new ValidationException("rinaSaksnummer kan ikke være tom");
        }

        String rinaSaksnummer = saksrelasjonDto.getRinaSaksnummer();
        Optional<FagsakRinasakKobling> eksisterende = saksrelasjonService.finnVedRinaSaksnummer(rinaSaksnummer);

        if (eksisterende.isPresent() && !eksisterende.get().getGsakSaksnummer().equals(saksrelasjonDto.getGsakSaksnummer())) {
            throw new ValidationException("Rinasak " + saksrelasjonDto.getGsakSaksnummer() +
                    " er allerede koblet mot gsakSaksnummer " + eksisterende.get().getGsakSaksnummer());
        }
    }

    private BUC hentBuc(String rinaSaksnummer) {
        try {
            return euxService.hentBuc(rinaSaksnummer);
        } catch (IntegrationException e) {
            log.error("Kunne ikke hente BUC {}", rinaSaksnummer, e);
            return null;
        }
    }
}
