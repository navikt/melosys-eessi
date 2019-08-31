package no.nav.melosys.eessi.controller;

import java.util.Map;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.OpprettSedDto;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.controller.dto.SvarAnmodningUnntakDto;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.service.sed.SedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping
public class MelosysEessiController {

    private final SedService sedService;

    @Autowired
    public MelosysEessiController(SedService sedService) {
        this.sedService = sedService;
    }

    @PostMapping("/sed/createAndSend")
    public Map<String, String> opprettOgSendSed(@RequestBody SedDataDto sedDataDto)
            throws IntegrationException, NotFoundException, MappingException {
        log.info("/api/sed/createAndSend: Oppretter ny buc og sed");

        String rinaCaseId = sedService.createAndSend(sedDataDto);
        Map<String, String> result = Maps.newHashMap();
        result.put("rinaCaseId", rinaCaseId);
        return result;
    }

    @PostMapping("/sed/create/{bucType}")
    public OpprettSedDto opprettUtkast(@RequestBody SedDataDto sedDataDto, @PathVariable BucType bucType)
            throws MappingException, IntegrationException, NotFoundException {

        log.info("/api/sed/create/{}: Oppretter sed", bucType);
        return sedService.createSed(sedDataDto, bucType);
    }

    @PostMapping("/buc/LA_BUC_01/{rinaId}/svar")
    public void anmodningUnntakSvarInnvilgelse(@RequestBody SvarAnmodningUnntakDto svarAnmodningUnntakDto, @PathVariable String rinaId)
            throws IntegrationException, NotFoundException {

        log.info("/buc/LA_BUC_01/{}/svar: Sender svar på AOU", rinaId);
        sedService.anmodningUnntakSvar(svarAnmodningUnntakDto, rinaId);
    }
}
