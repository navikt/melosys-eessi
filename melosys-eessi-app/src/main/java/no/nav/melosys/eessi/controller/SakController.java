package no.nav.melosys.eessi.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.BucinfoDto;
import no.nav.melosys.eessi.service.buc.BucService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/sak")
public class SakController {

    private final BucService bucService;

    @Autowired
    public SakController(BucService bucService) {
        this.bucService = bucService;
    }

    @GetMapping("/{gsakSaksnummer}/bucer")
    public List<BucinfoDto> hentTilknyttedeBucer(@PathVariable Long gsakSaksnummer,
                                                 @RequestParam(required = false) String status) {
        return bucService.hentBucer(gsakSaksnummer, status);
    }
}
