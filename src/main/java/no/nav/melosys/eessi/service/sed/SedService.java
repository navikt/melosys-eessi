package no.nav.melosys.eessi.service.sed;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.CreateSedDto;
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.sed.helpers.LovvalgSedMapperFactory;
import no.nav.melosys.eessi.service.sed.mapper.LovvalgSedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SedService {

    private final EuxService euxService;

    @Autowired
    public SedService(EuxService euxService) {
        this.euxService = euxService;
    }

    public String createAndSend(SedDataDto sedDataDto) throws MappingException, IntegrationException, NotFoundException {

        Long gsakSaksnummer = getGsakSaksnummer(sedDataDto);

        log.info("Oppretter buc og sed, gsakSaksnummer: {}", gsakSaksnummer);
        BucType bucType = SedUtils.getBucTypeFromBestemmelse(
                sedDataDto.getLovvalgsperioder().get(0).getBestemmelse());
        SedType sedType = SedUtils.getSedTypeFromBestemmelse(
                sedDataDto.getLovvalgsperioder().get(0).getBestemmelse());
        LovvalgSedMapper sedMapper = LovvalgSedMapperFactory.sedMapper(sedType);

        SED sed = sedMapper.mapTilSed(sedDataDto);

        return euxService.opprettOgSendBucOgSed(gsakSaksnummer, bucType.name(), getMottakerLand(sedDataDto), sed);
    }

    /**
     * Oppretter en SED på en ny BUC.
     *
     * @param sedDataDto sed som skal opprettes
     * @param bucType    hvilken type buc som skal opprettes (dersom det ikke er en eksisterende buc på saken)
     * @param sedType    hvilken type sed som skal opprettes
     * @return Dto-objekt som inneholder bucId, sedId og link til sak i rina
     */
    public CreateSedDto createSed(SedDataDto sedDataDto, BucType bucType)
            throws MappingException, NotFoundException, IntegrationException {

        SedType sedType = SedUtils.hentFoersteLovligeSedPaaBuc(bucType);

        String rinaSaksnummer;
        Long gsakSaksnummer = getGsakSaksnummer(sedDataDto);
        LovvalgSedMapper sedMapper = LovvalgSedMapperFactory.sedMapper(sedType);
        SED sed = sedMapper.mapTilSed(sedDataDto);

        log.info("Oppretter buc og sed, gsakSaksnummer: {}", gsakSaksnummer);
        rinaSaksnummer = euxService.opprettBucOgSed(gsakSaksnummer, bucType.name(), getMottakerLand(sedDataDto), sed);

        return CreateSedDto.builder()
                .bucId(rinaSaksnummer)
                .rinaUrl(euxService.hentRinaUrl(rinaSaksnummer))
                .build();
    }

    private Long getGsakSaksnummer(SedDataDto sedDataDto) throws MappingException {
        return Optional.ofNullable(sedDataDto.getGsakSaksnummer()).orElseThrow(() -> new MappingException("GsakId er påkrevd!"));
    }

    private String getMottakerLand(SedDataDto sedDataDto) throws NotFoundException {
        return sedDataDto.getLovvalgsperioder().stream().map(Lovvalgsperiode::getUnntakFraLovvalgsland)
                .findFirst().orElseThrow(() -> new NotFoundException("Landkode for lovvalg ikke satt"));
    }
}
