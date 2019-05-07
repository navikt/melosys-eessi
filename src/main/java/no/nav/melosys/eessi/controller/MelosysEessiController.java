package no.nav.melosys.eessi.controller;

import java.util.Map;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.service.sed.SedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/sed")
public class MelosysEessiController {

    private final SedService sendSedService;

    @Autowired
    public MelosysEessiController(SedService sendSedService) {
        this.sendSedService = sendSedService;
    }

    @PostMapping("/createAndSend")
    public Map<String, String> createAndSendCase(@RequestBody SedDataDto sedDataDto) throws Exception {
        log.info("/api/sed/createAndSend: Oppretter ny buc og sed");

        try {
            String rinaCaseId = sendSedService.createAndSend(sedDataDto);
            Map<String, String> result = Maps.newHashMap();
            result.put("rinaCaseId", rinaCaseId);
            return result;
        } catch (Exception e) {
            log.error("Error in /sed/createAndSend", e);
            throw e;
        }

    }

    @PostMapping("/createA008")
    public Map<String, String> createA008(@RequestBody SedDataDto sedDataDto,
                                          @RequestParam(required = false) String rinaSakId) throws MappingException, NotFoundException, IntegrationException {
        log.info("/api/sed/createA008: Oppretter og sender sed A008");

        try {
            return sendSedService.createA008(sedDataDto, rinaSakId);
        } catch (Exception e) {
            log.error("Error in /sed/createA008", e);
            throw e;
        }
    }
}
