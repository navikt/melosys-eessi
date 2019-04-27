package no.nav.melosys.eessi.controller;

import java.util.Map;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.service.sed.SendSedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/sed")
public class MelosysEessiController {

    private final SendSedService sendSedService;

    @Autowired
    public MelosysEessiController(SendSedService sendSedService) {
        this.sendSedService = sendSedService;
    }

    @PostMapping("/createAndSend")
    public Map<String, String> createAndSendCase(@RequestBody SedDataDto sedDataDto) throws Exception {
        log.info("/api/createAndSend: Oppretter ny buc og sed");

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
}
