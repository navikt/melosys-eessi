package no.nav.melosys.eessi.service.buc;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.BucinfoDto;
import no.nav.melosys.eessi.controller.dto.SedStatus;
import no.nav.melosys.eessi.controller.dto.SedinfoDto;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Document;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.service.caserelation.SaksrelasjonService;
import no.nav.melosys.eessi.service.eux.EuxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class BucService {

    private final EuxService euxService;

    private final SaksrelasjonService saksrelasjonService;

    @Autowired
    public BucService(EuxService euxService, SaksrelasjonService saksrelasjonService) {
        this.euxService = euxService;
        this.saksrelasjonService = saksrelasjonService;
    }

    public List<BucinfoDto> hentBucer(Long gsakSaksnummer, String status) {
        return saksrelasjonService.finnVedGsakSaksnummer(gsakSaksnummer).stream()
                .map(FagsakRinasakKobling::getRinaSaksnummer)
                .map(this::hentBuc)
                .filter(Objects::nonNull)
                .map(buc -> tilBucinfoDto(buc, status))
                .collect(Collectors.toList());
    }

    private BUC hentBuc(String rinaSaksnummer) {
        try {
            return euxService.hentBuc(rinaSaksnummer);
        } catch (IntegrationException e) {
            log.error("Kunne ikke hente BUC {}", rinaSaksnummer, e);
            return null;
        }
    }

    private BucinfoDto tilBucinfoDto(BUC buc, String status) {
        return BucinfoDto.builder()
                .id(buc.getId())
                .bucType(buc.getBucType())
                .opprettetDato(tilLocalDate(buc.getStartDate()))
                .seder(buc.getDocuments().stream()
                        .filter(filtrerMedStatus(status))
                        .map(doc -> tilSedinfoDto(doc, buc.getId()))
                        .collect(Collectors.toList()))
                .build();
    }

    private SedinfoDto tilSedinfoDto(Document sed, String bucId) {
        return SedinfoDto.builder()
                .bucId(bucId)
                .sedId(sed.getId())
                .sedType(sed.getType())
                .opprettetDato(tilLocalDate(sed.getCreationDate()))
                .sistOppdatert(tilLocalDate(sed.getLastUpdate()))
                .status(tilNorskStatus(sed.getStatus()))
                .rinaUrl(euxService.hentRinaUrl(bucId))
                .build();
    }

    private static String tilNorskStatus(String status) {
        SedStatus sedStatus = SedStatus.fraEngelskStatus(status);
        if (sedStatus == null) {
            return "";
        }

        return sedStatus.getNorskStatus();
    }

    private static LocalDate tilLocalDate(Long timestamp) {
        return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static Predicate<Document> filtrerMedStatus(String status) {
        if (StringUtils.isEmpty(status)) {
            return b -> true;
        }

        return b -> SedStatus.fraEngelskStatus(b.getStatus()) == SedStatus.fraNorskStatus(status);
    }
}
