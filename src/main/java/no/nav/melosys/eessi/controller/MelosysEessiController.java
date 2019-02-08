package no.nav.melosys.eessi.controller;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MelosysEessiController {

  @PostMapping("/createAndSend/{fagsakId}")
  public void createAndSendCase(@RequestBody SedDataDto sedDataDto) {

  }

}
