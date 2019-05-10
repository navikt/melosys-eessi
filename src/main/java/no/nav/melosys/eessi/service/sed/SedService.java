package no.nav.melosys.eessi.service.sed;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.CreateSedDto;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.caserelation.SaksrelasjonService;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.eux.OpprettBucOgSedResponse;
import no.nav.melosys.eessi.service.sed.helpers.LovvalgSedMapperFactory;
import no.nav.melosys.eessi.service.sed.mapper.LovvalgSedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SedService {

    private final EuxService euxService;
    private final SaksrelasjonService saksrelasjonService;

    @Autowired
    public SedService(EuxService euxService,
            SaksrelasjonService saksrelasjonService) {
        this.euxService = euxService;
        this.saksrelasjonService = saksrelasjonService;
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

        OpprettBucOgSedResponse opprettBucOgSedResponse = opprettOgLagreSaksrelasjon(bucType, sed, gsakSaksnummer, sedDataDto.getMottakerLand());

        try {
            euxService.sendSed(opprettBucOgSedResponse.getRinaSaksnummer(), opprettBucOgSedResponse.getDokumentId());
        } catch (IntegrationException e) {
            log.error("Feil ved oppretting og/eller sending av buc og sed. Exception fanges for å slette saksrelasjon.");
            euxService.slettBuC(opprettBucOgSedResponse.getRinaSaksnummer());
            saksrelasjonService.slettVedRinaId(opprettBucOgSedResponse.getRinaSaksnummer());
            throw e;
        }

        return opprettBucOgSedResponse.getRinaSaksnummer();
    }

    /**
     * Oppretter en SED på en ny BUC.
     *
     * @param sedDataDto sed som skal opprettes
     * @param bucType    hvilken type buc som skal opprettes (dersom det ikke er en eksisterende buc på saken)
     * @param sedType    hvilken type sed som skal opprettes
     * @return Dto-objekt som inneholder rinaSaksnummer, dokumentId og link til sak i rina
     */
    public CreateSedDto createSed(SedDataDto sedDataDto, BucType bucType)
            throws MappingException, NotFoundException, IntegrationException {

        SedType sedType = SedUtils.hentFoersteLovligeSedPaaBuc(bucType);

        Long gsakSaksnummer = getGsakSaksnummer(sedDataDto);
        LovvalgSedMapper sedMapper = LovvalgSedMapperFactory.sedMapper(sedType);
        SED sed = sedMapper.mapTilSed(sedDataDto);

        log.info("Oppretter buc og sed, gsakSaksnummer: {}", gsakSaksnummer);
        OpprettBucOgSedResponse opprettBucOgSedResponse = opprettOgLagreSaksrelasjon(bucType, sed, gsakSaksnummer, sedDataDto.getMottakerLand());

        return CreateSedDto.builder()
                .bucId(opprettBucOgSedResponse.getRinaSaksnummer())
                .rinaUrl(euxService.hentRinaUrl(opprettBucOgSedResponse.getRinaSaksnummer()))
                .build();
    }

    private OpprettBucOgSedResponse opprettOgLagreSaksrelasjon(BucType bucType, SED sed, Long gsakSaksnummer, String mottakerLand)
            throws NotFoundException, IntegrationException {
        OpprettBucOgSedResponse opprettBucOgSedResponse = euxService.opprettBucOgSed(bucType.name(), mottakerLand, sed);
        saksrelasjonService.lagreKobling(gsakSaksnummer, opprettBucOgSedResponse.getRinaSaksnummer(), bucType);
        log.info("gsakSaksnummer {} lagret med rinaId {}", gsakSaksnummer, opprettBucOgSedResponse.getRinaSaksnummer());
        return opprettBucOgSedResponse;
    }

    private Long getGsakSaksnummer(SedDataDto sedDataDto) throws MappingException {
        return Optional.ofNullable(sedDataDto.getGsakSaksnummer()).orElseThrow(() -> new MappingException("GsakId er påkrevd!"));
    }
}
