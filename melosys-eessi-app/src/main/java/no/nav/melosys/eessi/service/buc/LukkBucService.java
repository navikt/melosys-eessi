package no.nav.melosys.eessi.service.buc;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.metrikker.BucMetrikker;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Document;
import no.nav.melosys.eessi.models.bucinfo.BucInfo;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.eux.BucSearch;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.sed.mapper.til_sed.administrativ.X001Mapper;
import org.springframework.stereotype.Service;

import static no.nav.melosys.eessi.models.buc.SedVersjonSjekker.verifiserSedVersjonErBucVersjon;

@Service
@Slf4j
public class LukkBucService {

    private final EuxService euxService;
    private final X001Mapper x001Mapper;
    private final BucMetrikker bucMetrikker;

    public LukkBucService(EuxService euxService, BucMetrikker bucMetrikker) {
        this.euxService = euxService;
        this.x001Mapper = new X001Mapper();
        this.bucMetrikker = bucMetrikker;
    }

    public void lukkBucerAvType(BucType bucType) {
        try {
            log.info("Lukker bucer av type {}", bucType);
            euxService.hentBucer(BucSearch.builder().bucType(bucType.name()).build()) //FIXME: søk på BUC fungerer ikke med status open. Venter på eux/rina-fix
                    .stream()
                    .filter(BucInfo::bucErÅpen)
                    .filter(BucInfo::norgeErCaseOwner)
                    .map(BucInfo::getId)
                    .map(this::finnBuc)
                    .flatMap(Optional::stream)
                    .filter(Objects::nonNull)
                    .filter(BUC::kanLukkesAutomatisk)
                    .forEach(this::lukkBuc);
        } catch (IntegrationException e) {
            log.error("Feil ved henting av bucer av type {}", bucType, e);
        }
    }

    /*
    Async for at ekstern tjeneste ikke skal trenge å vente på resultat herfra.
    Blir kalt eksternt for å indikere at en tilhørende behandling er avsluttet, og at man kan anse utveksling som ferdig.
    Kan fortsatt ikke garantere at RINA har tilgjengeliggjort lukking av BUCen (create X001)
     */
    public void forsøkLukkBucAsync(final String rinaSaksnummer) {
        try {
            finnBuc(rinaSaksnummer)
                    .filter(b -> b.kanOppretteEllerOppdatereSed(SedType.X001))
                    .ifPresentOrElse(
                            this::lukkBuc,
                            () -> log.info("Ikke mulig å opprette X001 i rina-sak {}", rinaSaksnummer)
                    );
        } catch (Exception e) {
            log.warn("Feil ved forsøk av lukking av BUC {}", rinaSaksnummer);
        }
    }

    private void lukkBuc(BUC buc) {
        try {
            SED x001 = opprettX001(buc, LukkBucAarsakMapper.hentAarsakForLukking(buc));
            verifiserSedVersjonErBucVersjon(buc, x001);

            finnEksisterendeX001Utkast(buc).ifPresentOrElse(
                    eksisterendeX001 -> {
                        euxService.oppdaterSed(buc.getId(), eksisterendeX001.getId(), x001);
                        euxService.sendSed(buc.getId(), eksisterendeX001.getId());
                    },
                    () -> euxService.opprettOgSendSed(x001, buc.getId())
            );

            bucMetrikker.bucLukket(buc.getBucType());
            log.info("BUC {} lukket med årsak {}", buc.getId(), x001.getNav().getSak().getAnmodning().getAvslutning().getAarsak().getType());
        } catch (IntegrationException e) {
            log.error("Kunne ikke lukke buc {}", buc.getId(), e);
        }
    }

    private Optional<Document> finnEksisterendeX001Utkast(BUC buc) {
        return buc.getDocuments().stream()
                .filter(Document::erX001)
                .filter(Document::erOpprettet)
                .min(documentComparator);
    }

    private SED opprettX001(BUC buc, String aarsak) {
        return x001Mapper.mapFraSed(hentSisteLovvalgSed(buc), aarsak);
    }

    private SED hentSisteLovvalgSed(BUC buc) {
        return buc.getDocuments().stream()
                .filter(Document::sedErSendt)
                .min(documentComparator)
                .map(d -> euxService.hentSed(buc.getId(), d.getId()))
                .orElseThrow(() -> new IllegalStateException("Finner ingen lovvalgs-SED på buc" + buc.getId()));
    }

    private Optional<BUC> finnBuc(String rinaSaksnummer) {
        try {
            return Optional.of(euxService.hentBuc(rinaSaksnummer));
        } catch (IntegrationException ex) {
            return Optional.empty();
        }
    }

    private static final Comparator<Document> documentComparator = Comparator.comparing(Document::getCreationDate);
}
