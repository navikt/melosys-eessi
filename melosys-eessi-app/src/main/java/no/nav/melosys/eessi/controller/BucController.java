package no.nav.melosys.eessi.controller;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.InstitusjonDto;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.service.eux.EuxService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/buc")
public class BucController {

    private final EuxService euxService;

    public BucController(EuxService euxService) {
        this.euxService = euxService;
    }

    @GetMapping("/{bucType}/institusjoner")
    public List<InstitusjonDto> hentMottakerinstitusjoner(@PathVariable BucType bucType,
                                                          @RequestParam(required = false) String land) throws IntegrationException {
        return euxService.hentMottakerinstitusjoner(bucType.name(), land).stream()
                .map(institusjon -> new InstitusjonDto(institusjon.getId(), institusjon.getNavn(), institusjon.getLandkode()))
                .collect(Collectors.toList());
    }
}
