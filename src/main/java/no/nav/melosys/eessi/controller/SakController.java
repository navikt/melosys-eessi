package no.nav.melosys.eessi.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.SedinfoDto;
import no.nav.melosys.eessi.service.sed.SedService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/sak")
public class SakController {

    private final SedService sedService;

    public SakController(SedService sedService) {
        this.sedService = sedService;
    }

    @GetMapping("/{gsakSaksnummer}/sed")
    public List<SedinfoDto> hentTilknyttedeSeder(@PathVariable Long gsakSaksnummer,
                                                 @RequestParam(required = false) String status) {
        return sedService.hentSeder(gsakSaksnummer, status);
    }
}
