package no.nav.melosys.eessi.controller;

import java.util.Map;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.CreateSedDto;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.exception.ValidationException;
import no.nav.melosys.eessi.service.sed.SedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/sed")
public class MelosysEessiController {

    private final SedService sedService;

    @Autowired
    public MelosysEessiController(SedService sedService) {
        this.sedService = sedService;
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

    @PostMapping("/create/{bucType}/{sedType}")
    public CreateSedDto create(@RequestBody SedDataDto sedDataDto,
                               @PathVariable BucType bucType,
                               @PathVariable(required = false) SedType sedType)
            throws MappingException, IntegrationException, NotFoundException, ValidationException {
        log.info("/api/sed/create/{}/{}: Oppretter sed", bucType, sedType);

        try {
            if (bucType == null || sedType == null) {
                throw new ValidationException("Kan ikke opprette sed med bucType " + bucType + " og sedType " + sedType);
            }
            return sedService.createSed(sedDataDto, bucType, sedType);
        } catch (MappingException | NotFoundException | IntegrationException | ValidationException e) {
            log.error("Error in /sed/createAndSend", e);
            throw e;
        }
    }
}
