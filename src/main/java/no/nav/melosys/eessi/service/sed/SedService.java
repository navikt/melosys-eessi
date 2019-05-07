package no.nav.melosys.eessi.service.sed;

import java.util.Map;
import java.util.Optional;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
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
import no.nav.melosys.eessi.service.sed.mapper.A008Mapper;
import no.nav.melosys.eessi.service.sed.mapper.LovvalgSedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
     * Oppretter en Sed A008 og returnerer rinaSakId og link til sak i rina.
     * Sed-en blir opprettet på en eksisterende Buc dersom den er oppgitt eller dersom
     * den finnes i CaseRelationRepository.
     *
     * @param sedDataDto data for sed
     * @param rinaCaseId oppretter A008 på denne Buc-en, dersom den er oppgitt
     * @return map med rinaSakId og url til SED i Rina
     */
    public Map<String, String> createA008(SedDataDto sedDataDto, String rinaCaseId) throws MappingException, NotFoundException, IntegrationException {

        String sedId = null;
        Long gsakSaksnummer = getGsakSaksnummer(sedDataDto);
        LovvalgSedMapper sedMapper = new A008Mapper();
        SED sed = sedMapper.mapTilSed(sedDataDto);

        if (!StringUtils.isEmpty(rinaCaseId)) {
            log.info("Oppretter sed på eksisterende buc {}, gsakSaksnummer: {}", rinaCaseId, gsakSaksnummer);
            sedId = euxService.opprettSed(sed, rinaCaseId);
        } else {
            Optional<CaseRelation> caseRelation = caseRelationService.findByGsakSaksnummer(gsakSaksnummer);
            if (caseRelation.isPresent()) { // Finnes allerede en buc på sak
                rinaCaseId = caseRelation.get().getRinaId();
                log.info("Oppretter sed på eksisterende buc {}, gsakSaksnummer: {}", rinaCaseId, gsakSaksnummer);
                sedId = euxService.opprettSed(sed, rinaCaseId);
            } else { // Oppretter ny buc og sed
                log.info("Oppretter buc og sed, gsakSaksnummer: {}", gsakSaksnummer);
                rinaCaseId = euxService.opprettBucOgSed(gsakSaksnummer, BucType.LA_BUC_03.name(), getMottakerLand(sedDataDto), sed);
            }
        }

        Map<String, String> result = Maps.newHashMap();
        result.put("rinaCaseId", rinaCaseId);
        result.put("rinaUrl", euxService.hentRinaUrl(rinaCaseId, sedId));
        return result;
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
