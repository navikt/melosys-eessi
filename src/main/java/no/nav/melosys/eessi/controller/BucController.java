package no.nav.melosys.eessi.controller;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.BucSedRelasjonDto;
import no.nav.melosys.eessi.controller.dto.InstitusjonDto;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.sed.BucService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/buc")
public class BucController {

    private final EuxService euxService;

    private final BucService bucService;

    public BucController(EuxService euxService, BucService bucService) {
        this.euxService = euxService;
        this.bucService = bucService;
    }

    @GetMapping("/mottakerinstitusjoner/{bucType}")
    public List<InstitusjonDto> hentMottakerinstitusjoner(@PathVariable BucType bucType,
                                                          @RequestParam(required = false) String land) throws IntegrationException {
        return euxService.hentAlleMottakerinstitusjoner(bucType.name()).stream()
                .filter(institusjon -> StringUtils.isEmpty(land) || land.equalsIgnoreCase(institusjon.getLandkode()))
                .map(institusjon -> InstitusjonDto.builder()
                        .id(institusjon.getId())
                        .navn(institusjon.getNavn())
                        .landkode(institusjon.getLandkode())
                        .build())
                .collect(Collectors.toList());
    }

    @GetMapping("/bucSedRelasjoner")
    public List<BucSedRelasjonDto> hentBucSedRelasjoner() {
        return bucService.hentBucSedRelasjoner();
    }
}
