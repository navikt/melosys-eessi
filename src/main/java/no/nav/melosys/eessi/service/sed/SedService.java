package no.nav.melosys.eessi.service.sed;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.CreateSedDto;
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.CaseRelation;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.BucType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.SedType;
import no.nav.melosys.eessi.service.caserelation.CaseRelationService;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.sed.helpers.LovvalgSedMapperFactory;
import no.nav.melosys.eessi.service.sed.mapper.LovvalgSedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SedService {

    private final EuxService euxService;

    private final CaseRelationService caseRelationService;

    @Autowired
    public SedService(EuxService euxService, CaseRelationService caseRelationService) {
        this.euxService = euxService;
        this.caseRelationService = caseRelationService;
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
     * Oppretter en Sed.
     * Sed-en blir opprettet på en eksisterende Buc dersom den finnes i CaseRelationRepository,
     * hvis ikke blir det opprettet en ny Buc av type bucType.
     *
     * @param sedDataDto sed som skal opprettes
     * @param bucType    hvilken type buc som skal opprettes (dersom det ikke er en eksisterende buc på saken)
     * @param sedType    hvilken type sed som skal opprettes
     * @return Dto-objekt som inneholder bucId, sedId og link til sak i rina
     */
    public CreateSedDto createSed(SedDataDto sedDataDto, BucType bucType, SedType sedType)
            throws MappingException, NotFoundException, IntegrationException {

        String sedId = null;
        String rinaSaksnummer;
        Long gsakSaksnummer = getGsakSaksnummer(sedDataDto);
        LovvalgSedMapper sedMapper = LovvalgSedMapperFactory.sedMapper(sedType);
        SED sed = sedMapper.mapTilSed(sedDataDto);

        Optional<CaseRelation> caseRelation = caseRelationService.findByGsakSaksnummer(gsakSaksnummer);
        if (caseRelation.isPresent()) { // Finnes allerede en buc på sak
            rinaSaksnummer = caseRelation.get().getRinaId();
            if (euxService.sedKanOpprettesPaaBuc(rinaSaksnummer, sedType)) {
                log.info("Oppretter sed på eksisterende buc {}, gsakSaksnummer: {}", rinaSaksnummer, gsakSaksnummer);
                sedId = euxService.opprettSed(sed, rinaSaksnummer);
            } else {
                throw new IntegrationException("Kunne ikke opprette SED av type " + sedType + " på BUC " + rinaSaksnummer);
            }
        } else { // Oppretter ny buc og sed
            log.info("Oppretter buc og sed, gsakSaksnummer: {}", gsakSaksnummer);
            rinaSaksnummer = euxService.opprettBucOgSed(gsakSaksnummer, bucType.name(), getMottakerLand(sedDataDto), sed);
        }

        return CreateSedDto.builder()
                .bucId(rinaSaksnummer)
                .sedId(sedId)
                .rinaUrl(euxService.hentRinaUrl(rinaSaksnummer, sedId))
                .build();
    }

    private Long getGsakSaksnummer(SedDataDto sedDataDto) throws MappingException {
        Long gsakSaksnummer = sedDataDto.getGsakSaksnummer();
        if (gsakSaksnummer == null) {
            log.error("sakId er null, kan ikke opprette buc og sed");
            throw new MappingException("GsakId er påkrevd!");
        }
        return gsakSaksnummer;
    }

    private String getMottakerLand(SedDataDto sedDataDto) throws NotFoundException {
        return sedDataDto.getLovvalgsperioder().stream().map(Lovvalgsperiode::getUnntakFraLovvalgsland)
                .findFirst().orElseThrow(() -> new NotFoundException("Landkode for lovvalg ikke satt"));
    }
}
