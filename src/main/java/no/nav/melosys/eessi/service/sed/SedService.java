package no.nav.melosys.eessi.service.sed;

import java.util.Map;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.integration.eux.EuxConsumer;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.sed.BucType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.SedType;
import no.nav.melosys.eessi.service.sed.helpers.SedDataMapperRuter;
import no.nav.melosys.eessi.service.sed.mapper.SedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SedService {

    private final EuxConsumer euxConsumer;

    @Autowired
    public SedService(EuxConsumer euxConsumer) {
        this.euxConsumer = euxConsumer;
    }

    public String createAndSend(SedDataDto sedDataDto) throws MappingException, IntegrationException {

        BucType bucType = SedUtils.getBucTypeFromBestemmelse(
                sedDataDto.getLovvalgsperioder().get(0).getBestemmelse());
        SedType sedType = SedUtils.getSedTypeFromBestemmelse(
                sedDataDto.getLovvalgsperioder().get(0).getBestemmelse());
        SedMapper sedMapper = SedDataMapperRuter.sedMapper(sedType);

        SED sed = sedMapper.mapTilSed(sedDataDto);

        Map<String, String> map = euxConsumer.opprettBucOgSed(bucType.name(), "NAVT003",
                sed); //NAVT003 vil være default i test-fase
        String rinaCaseId = map.get("caseId");
        String documentId = map.get("documentId");

        euxConsumer.sendSed(rinaCaseId, "!23", documentId);

        return rinaCaseId;
    }
}
