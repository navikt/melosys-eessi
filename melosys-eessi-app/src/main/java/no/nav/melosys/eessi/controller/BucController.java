package no.nav.melosys.eessi.controller;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.*;
import no.nav.melosys.eessi.integration.eux.rina_api.EuxConsumer;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.ValidationException;
import no.nav.melosys.eessi.service.buc.LukkBucService;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.sed.SedService;
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag.SedGrunnlagMapperFactory;
import no.nav.security.token.support.core.api.Protected;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;

@Protected
@Slf4j
@RestController
@RequestMapping("/buc")
public class BucController {

    private final EuxService euxService;
    private final SedService sedService;
    private final LukkBucService lukkBucService;
    private final EuxConsumer euxConsumer;

    public BucController(@Qualifier("tokenContext") EuxService euxService,
                         SedService sedService,
                         LukkBucService lukkBucService,
                         EuxConsumer euxConsumer) {
        this.euxService = euxService;
        this.sedService = sedService;
        this.lukkBucService = lukkBucService;
        this.euxConsumer = euxConsumer;
    }

    // @ApiOperation(value = "Oppretter første SED for den spesifikke buc-typen, og sender denne hvis sendAutomatisk=true. " +
    //    "Sender på eksisterende BUC hvis BUCen meddeler et lovvalg med utenlandsk myndighet, og BUCen er åpen.")
    @PostMapping(
        value = "/{bucType}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public BucOgSedOpprettetDto opprettBucOgSed(
        @RequestBody OpprettBucOgSedDto opprettBucOgSedDto,
        @PathVariable("bucType") BucType bucType,
        @RequestParam(value = "sendAutomatisk") boolean sendAutomatisk,
        @RequestParam(value = "oppdaterEksisterende", required = false) boolean oppdaterEksisterende
    ) throws ValidationException {
        if (bucType.hentFørsteLovligeSed().kreverAdresse() && opprettBucOgSedDto.getSedDataDto().manglerAdresser()) {
            throw new ValidationException("Personen mangler adresse");
        }
        return sedService.opprettBucOgSed(
            opprettBucOgSedDto.getSedDataDto(),
            ofNullable(opprettBucOgSedDto.getVedlegg()).orElse(emptySet()),
            bucType,
            sendAutomatisk,
            oppdaterEksisterende);
    }

    //  @ApiOperation(value = "Oppretter og sender en sed på en eksisterende buc")
    @PostMapping("/{rinaSaksnummer}/sed/{sedType}")
    public void sendPåEksisterendeBuc(
        @RequestBody SedDataDto sedDataDto,
        @PathVariable String rinaSaksnummer,
        @PathVariable SedType sedType
    ) throws ValidationException {
        if (sedType.kreverAdresse() && sedDataDto.manglerAdresser()) {
            throw new ValidationException("Personen mangler adresse");
        }
        sedService.sendPåEksisterendeBuc(sedDataDto, rinaSaksnummer, sedType);
    }

    //    @ApiOperation(value = "Henter mottakerinstitusjoner som er satt som EESSI-klare for den spesifikke buc-type")
    @GetMapping("/{bucType}/institusjoner")
    public List<InstitusjonDto> hentMottakerinstitusjoner(@PathVariable BucType bucType,
                                                          @RequestParam(required = false) Collection<String> land) {
        return euxService.hentMottakerinstitusjoner(bucType.name(), land).stream()
            .map(institusjon -> new InstitusjonDto(institusjon.getId(), institusjon.getNavn(), institusjon.getLandkode()))
            .collect(Collectors.toList());
    }

    // @ApiOperation(value = "Henter sedGrunnlag for gitt sed")
    @GetMapping("/{rinaSaksnummer}/sed/{rinaDokumentId}/grunnlag")
    public SedGrunnlagDto hentSedGrunnlag(@PathVariable String rinaSaksnummer, @PathVariable String rinaDokumentId) {
        var sed = euxService.hentSed(rinaSaksnummer, rinaDokumentId);
        return SedGrunnlagMapperFactory.getMapper(SedType.valueOf(sed.getSedType())).map(sed);
    }

    // @ApiOperation(value = "Oppretter en asynkron jobb som forsøker å lukke en spesifikk BUC, om den har actions som tillater det")
    @PostMapping("/{rinaSaksnummer}/lukk")
    public void lukkBuc(@PathVariable("rinaSaksnummer") String rinaSaksnummer) {
        lukkBucService.forsøkLukkBucAsync(rinaSaksnummer);
    }

    // @ApiOperation(value = "Henter mulige aksjoner på en Buc")
    @GetMapping("/{rinaSaksnummer}/aksjoner")
    public Collection<String> hentMuligeBucHandlinger(@PathVariable("rinaSaksnummer") String rinaSaksnummer) {
        return euxConsumer.hentBucHandlinger(rinaSaksnummer);
    }
}
