package no.nav.melosys.eessi.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.InstitusjonDto;
import no.nav.melosys.eessi.controller.dto.OpprettSedDto;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.controller.dto.SedGrunnlagDto;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.SedVedlegg;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.ValidationException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.sed.SedService;
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag.SedGrunnlagMapperFactory;
import no.nav.security.token.support.core.api.Protected;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public OpprettSedDto opprettBucOgSed(
            @RequestPart("sedData") SedDataDto sedDataDto,
            @RequestPart(value = "vedlegg", required = false) MultipartFile vedlegg,
            @PathVariable("bucType") BucType bucType,
            @RequestParam(value = "sendAutomatisk") boolean sendAutomatisk
    ) throws IntegrationException, IOException, ValidationException {
        return sedService.opprettBucOgSed(sedDataDto, vedlegg != null ? new SedVedlegg(vedlegg.getOriginalFilename(), vedlegg.getBytes()) : null, bucType, sendAutomatisk);
    }

    @ApiOperation(value = "Oppretter og sender en sed på en eksisterende buc")
    @PostMapping("/{rinaSaksnummer}/sed/{sedType}")
    public void sendPåEksisterendeBuc(
            @RequestBody SedDataDto sedDataDto,
            @PathVariable String rinaSaksnummer,
            @PathVariable SedType sedType
    ) throws IntegrationException {
        sedService.sendPåEksisterendeBuc(sedDataDto, rinaSaksnummer, sedType);
    }

    @ApiOperation(value = "Henter mottakerinstitusjoner som er satt som EESSI-klare for den spesifikke buc-type")
    @GetMapping("/{bucType}/institusjoner")
    public List<InstitusjonDto> hentMottakerinstitusjoner(@PathVariable BucType bucType,
                                                          @RequestParam(required = false) String land) throws IntegrationException {
        return euxService.hentMottakerinstitusjoner(bucType.name(), land).stream()
                .map(institusjon -> new InstitusjonDto(institusjon.getId(), institusjon.getNavn(), institusjon.getLandkode()))
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "Henter sedGrunnlag for gitt sed")
    @GetMapping("/{rinaSaksnummer}/sed/{rinaDokumentId}/grunnlag")
    public SedGrunnlagDto hentSedGrunnlag(@PathVariable String rinaSaksnummer, @PathVariable String rinaDokumentId) throws IntegrationException {
        SED sed = euxService.hentSed(rinaSaksnummer, rinaDokumentId);
        return SedGrunnlagMapperFactory.getMapper(SedType.valueOf(sed.getSedType())).map(sed);
    }
}
