package no.nav.melosys.eessi.controller;

import java.util.Map;
import com.google.common.collect.Maps;
import lombok.extern.log4j.Log4j2;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.service.sed.SedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
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
        log.info("/api/createAndSend: Oppretter ny buc og sed");
        String rinaCaseId = sedService.createAndSend(sedDataDto);
        Map<String, String> result = Maps.newHashMap();
        result.put("rinaCaseId", rinaCaseId);

        return result;
    }
}
