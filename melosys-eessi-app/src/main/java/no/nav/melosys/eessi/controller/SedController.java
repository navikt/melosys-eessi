package no.nav.melosys.eessi.controller;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.ValidationException;
import no.nav.melosys.eessi.service.sed.SedService;
import no.nav.security.token.support.core.api.Protected;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Protected
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
    public byte[] genererPdfFraSed(@RequestBody SedDataDto sedDataDto, @PathVariable SedType sedType) throws ValidationException {
        if (sedType.kreverAdresse() && sedDataDto.manglerAdresser()) {
            throw new ValidationException(String.format("Personen mangler adresse ved PDF generering fra sedType=%s", sedType));
        }
        return sedService.genererPdfFraSed(sedDataDto, sedType);
    }
}
