package no.nav.melosys.eessi.service.sed;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.integration.eux.EuxConsumer;
import no.nav.melosys.eessi.models.CaseRelation;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.BucType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.SedType;
import no.nav.melosys.eessi.repository.CaseRelationRepository;
import no.nav.melosys.eessi.service.sed.helpers.SedDataMapperRuter;
import no.nav.melosys.eessi.service.sed.mapper.SedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SedService {

    private final EuxConsumer euxConsumer;
    private final CaseRelationRepository caseRelationRepository;

    @Autowired
    public SedService(EuxConsumer euxConsumer,
            CaseRelationRepository caseRelationRepository) {
        this.euxConsumer = euxConsumer;
        this.caseRelationRepository = caseRelationRepository;
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

        Map<String, String> map = euxConsumer.opprettBucOgSed(bucType.name(), "NAVT003", sed); //NAVT003 vil være default i test-fase
        String rinaCaseId = map.get("caseId");
        String documentId = map.get("documentId");
        log.info("Buc opprettet med id: {} og sed opprettet med id: {}", rinaCaseId, documentId);

        CaseRelation caseRelation = new CaseRelation();
        caseRelation.setRinaId(rinaCaseId);
        caseRelation.setGsakSaksnummer(sedDataDto.getGsakSaksnummer());
        caseRelationRepository.save(caseRelation);

        euxConsumer.sendSed(rinaCaseId, "!23", documentId);
        log.info("Sed {} sendt", documentId);

        log.info("gsakSaksnummer {} lagret med rinaId {}", gsakSaksnummer, rinaCaseId);

        return rinaCaseId;
    }
}
