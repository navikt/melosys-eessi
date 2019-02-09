package no.nav.melosys.eessi.controller;

import com.google.common.collect.Maps;
import java.util.Map;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.service.sed.SedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    String rinaCaseId = sedService.createAndSend(sedDataDto);
    Map<String, String> result = Maps.newHashMap();
    result.put("rinaCaseId", rinaCaseId);

    return result;
  }

}
