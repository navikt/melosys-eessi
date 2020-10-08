package no.nav.melosys.eessi.controller;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.*;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.ValidationException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.sed.SedService;
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag.SedGrunnlagMapperFactory;
import no.nav.security.token.support.core.api.Protected;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Protected
@Slf4j
@RestController
@RequestMapping("/buc")
public class BucController {

    private final EuxService euxService;
    private final SedService sedService;

    public BucController(@Qualifier("tokenContext") EuxService euxService, SedService sedService) {
        this.euxService = euxService;
        this.sedService = sedService;
    }

    @ApiOperation(value = "Oppretter første SED for den spesifikke buc-typen, og sender denne hvis sendAutomatisk=true. " +
            "Sender på eksisterende BUC hvis BUCen meddeler et lovvalg med utenlandsk myndighet, og BUCen er åpen.")
    @PostMapping(
            value = "/{bucType}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public BucOgSedOpprettetDto opprettBucOgSed(
            @RequestBody OpprettBucOgSedDto opprettBucOgSedDto,
            @PathVariable("bucType") BucType bucType,
            @RequestParam(value = "sendAutomatisk") boolean sendAutomatisk
    ) throws ValidationException {
        return sedService.opprettBucOgSed(opprettBucOgSedDto.getSedDataDto(), opprettBucOgSedDto.getVedlegg(), bucType, sendAutomatisk);
    }

    @ApiOperation(value = "Oppretter og sender en sed på en eksisterende buc")
    @PostMapping("/{rinaSaksnummer}/sed/{sedType}")
    public void sendPåEksisterendeBuc(
            @RequestBody SedDataDto sedDataDto,
            @PathVariable String rinaSaksnummer,
            @PathVariable SedType sedType
    )  {
        sedService.sendPåEksisterendeBuc(sedDataDto, rinaSaksnummer, sedType);
    }

    @ApiOperation(value = "Henter mottakerinstitusjoner som er satt som EESSI-klare for den spesifikke buc-type")
    @GetMapping("/{bucType}/institusjoner")
    public List<InstitusjonDto> hentMottakerinstitusjoner(@PathVariable BucType bucType,
                                                          @RequestParam(required = false) Collection<String> land)  {
        return euxService.hentMottakerinstitusjoner(bucType.name(), land).stream()
                .map(institusjon -> new InstitusjonDto(institusjon.getId(), institusjon.getNavn(), institusjon.getLandkode()))
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "Henter sedGrunnlag for gitt sed")
    @GetMapping("/{rinaSaksnummer}/sed/{rinaDokumentId}/grunnlag")
    public SedGrunnlagDto hentSedGrunnlag(@PathVariable String rinaSaksnummer, @PathVariable String rinaDokumentId)  {
        SED sed = euxService.hentSed(rinaSaksnummer, rinaDokumentId);
        return SedGrunnlagMapperFactory.getMapper(SedType.valueOf(sed.getSedType())).map(sed);
    }
}
