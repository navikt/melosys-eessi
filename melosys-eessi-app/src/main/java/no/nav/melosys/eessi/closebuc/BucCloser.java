package no.nav.melosys.eessi.closebuc;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Document;
import no.nav.melosys.eessi.models.bucinfo.BucInfo;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.eux.BucSearch;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.sed.mapper.X001Mapper;
import org.springframework.stereotype.Service;
import static no.nav.melosys.eessi.models.buc.BucUtils.*;

@Service
@Slf4j
public class BucCloser {

    private final EuxService euxService;
    private final X001Mapper x001Mapper;

    public BucCloser(EuxService euxService) {
        this.euxService = euxService;
        this.x001Mapper = new X001Mapper();
    }

    public void closeBucsByType(BucType bucType) {
        try {
            euxService.hentBucer(BucSearch.builder().bucType(bucType.name()).status("open").build())
                    .stream()
                    .map(this::hentBuc)
                    .filter(Objects::nonNull)
                    .filter(norgeErCaseOwnerPredicate)
                    .filter(bucKanLukkesPredicate)
                    .forEach(this::lukkBuc);

        } catch (IntegrationException e) {
            log.error("Feil ved henting av bucer av type {}", bucType);
        }
    }

    private void lukkBuc(BUC buc) {
        try {
            SED x001 = opprettX001(buc, LukkBucAarsakMapper.hentAarsakForLukking(buc));

            Document eksisterendeX001 = hentEksisterendeX001Utkast(buc);

            if (eksisterendeX001 == null) {
                euxService.opprettOgSendSed(x001, buc.getId());
            } else {
                euxService.oppdaterSed(buc.getId(), eksisterendeX001.getId(), x001);
                euxService.sendSed(buc.getId(), eksisterendeX001.getId());
            }

            log.info("BUC {} lukket med årsak {}", buc.getId(), x001.getNav().getSak().getAnmodning().getAvslutning().getAarsak().getType());
        } catch (IntegrationException e) {
            log.error("Kunne ikke lukke buc {}", buc.getId(), e);
        }
    }

    private Document hentEksisterendeX001Utkast(BUC buc) {
        return buc.getDocuments().stream()
                .filter(dokumentErX001Predicate)
                .min(documentComparator).orElse(null);
    }

    private SED opprettX001(BUC buc, String aarsak) throws IntegrationException {
        return x001Mapper.mapFraSed(hentSisteLovvalgSed(buc), aarsak);
    }

    private SED hentSisteLovvalgSed(BUC buc) throws IntegrationException {
        String sedId = buc.getDocuments().stream()
                .filter(sisteSendtLovvalgsSedPredicate)
                .min(documentComparator)
                .orElseThrow(() -> new IllegalStateException("Finner ingen lovvalgs-SED på buc" + buc.getId()))
                .getId();

        return euxService.hentSed(buc.getId(), sedId);
    }

    private BUC hentBuc(BucInfo bucInfo) {
        try {
            return euxService.hentBuc(bucInfo.getId());
        } catch (IntegrationException ex) {
            log.error("Feil ved henting av buc med id {}", bucInfo.getId(), ex);
        }

        return null;
    }

    private static final Comparator<Document> documentComparator = Comparator.comparing(d ->
            Instant.ofEpochMilli(d.getCreationDate()).atZone(ZoneId.systemDefault()).toLocalDate());
}
