package no.nav.melosys.eessi.controller;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.service.sed.SedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping
public class SedController {

    private final SedService sedService;

    @Autowired
    public SedController(SedService sedService) {
        this.sedService = sedService;
    }

    @PostMapping("/sed/{sedType}/pdf")
    public byte[] genererPdfFraSed(@RequestBody SedDataDto sedDataDto, @PathVariable SedType sedType)
            throws IntegrationException, NotFoundException, MappingException {

        return sedService.genererPdfFraSed(sedDataDto, sedType);
    }
}
