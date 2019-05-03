package no.nav.melosys.eessi.service.sed;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.BucType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.SedType;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.sed.helpers.SedDataTilLovvalgSedMapperFactory;
import no.nav.melosys.eessi.service.sed.mapper.LovvalgSedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SendSedService {

    private final EuxService euxService;

    @Autowired
    public SendSedService(EuxService euxService) {
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
        LovvalgSedMapper sedMapper = SedDataTilLovvalgSedMapperFactory.sedMapper(sedType);

        SED sed = sedMapper.mapTilSed(sedDataDto);
        String mottakerLand = sedDataDto.getLovvalgsperioder().stream().map(Lovvalgsperiode::getUnntakFraLovvalgsland)
                .findFirst().orElseThrow(() -> new NotFoundException("Landkode for lovvalg ikke satt"));

        return euxService.opprettOgSendBucOgSed(gsakSaksnummer, bucType.name(), mottakerLand, sed);
    }
}
