package no.nav.melosys.eessi.service.sed;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.BucType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.SedType;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.sed.helpers.SedDataMapperRuter;
import no.nav.melosys.eessi.service.sed.mapper.SedMapper;
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

        Long gsakSaksnummer = sedDataDto.getGsakSaksnummer();
        if (gsakSaksnummer == null) {
            log.error("sakId er null, kan ikke opprette buc og sed");
            throw new MappingException("GsakId er påkrevd!");
        }

        log.info("Oppretter buc og sed, gsakSaksnummer: {}", gsakSaksnummer);
        BucType bucType = SedUtils.getBucTypeFromBestemmelse(
                sedDataDto.getLovvalgsperioder().get(0).getBestemmelse());
        SedType sedType = SedUtils.getSedTypeFromBestemmelse(
                sedDataDto.getLovvalgsperioder().get(0).getBestemmelse());
        SedMapper sedMapper = SedDataMapperRuter.sedMapper(sedType);

        SED sed = sedMapper.mapTilSed(sedDataDto);

        //NAVT002 vil være default i test-fase
        return euxService.opprettOgSendBucOgSed(gsakSaksnummer, bucType.name(), "NAVT002", sed);
    }
}
