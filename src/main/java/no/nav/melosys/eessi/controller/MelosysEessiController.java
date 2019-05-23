package no.nav.melosys.eessi.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.CreateSedDto;
import no.nav.melosys.eessi.controller.dto.InstitusjonDto;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.sed.SedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/sed")
public class MelosysEessiController {

    private final SedService sedService;
    private final EuxService euxService;

    @Autowired
    public MelosysEessiController(SedService sedService, EuxService euxService) {
        this.sedService = sedService;
        this.euxService = euxService;
    }

    @PostMapping("/createAndSend")
    public Map<String, String> createAndSendCase(@RequestBody SedDataDto sedDataDto) throws Exception {
        log.info("/api/sed/createAndSend: Oppretter ny buc og sed");

        try {
            String rinaCaseId = sedService.createAndSend(sedDataDto);
            Map<String, String> result = Maps.newHashMap();
            result.put("rinaCaseId", rinaCaseId);
            return result;
        } catch (Exception e) {
            log.error("Error in /sed/createAndSend", e);
            throw e;
        }

    }

    @PostMapping("/create/{bucType}")
    public CreateSedDto create(@RequestBody SedDataDto sedDataDto, @PathVariable BucType bucType)
            throws MappingException, IntegrationException, NotFoundException {

        log.info("/api/sed/create/{}: Oppretter sed", bucType);

        try {
            return sedService.createSed(sedDataDto, bucType);
        } catch (MappingException | NotFoundException | IntegrationException e) {
            log.error("Error in /sed/createAndSend", e);
            throw e;
        }
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
}
