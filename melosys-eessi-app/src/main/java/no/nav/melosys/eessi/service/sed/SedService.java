package no.nav.melosys.eessi.service.sed;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.OpprettSedDto;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.controller.dto.SedStatus;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Document;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.eux.OpprettBucOgSedResponse;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import no.nav.melosys.eessi.service.sed.helpers.SedMapperFactory;
import no.nav.melosys.eessi.service.sed.mapper.SedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SedService {

    private final EuxService euxService;
    private final SaksrelasjonService saksrelasjonService;

    @Autowired
    public SedService(@Qualifier("tokenContext") EuxService euxService,
                      SaksrelasjonService saksrelasjonService) {
        this.euxService = euxService;
        this.saksrelasjonService = saksrelasjonService;
    }

    public OpprettSedDto opprettBucOgSed(SedDataDto sedDataDto, byte[] vedlegg, BucType bucType, boolean sendAutomatisk)
            throws MappingException, IntegrationException, NotFoundException {

        Long gsakSaksnummer = hentGsakSaksnummer(sedDataDto);
        log.info("Oppretter buc og sed, gsakSaksnummer: {}", gsakSaksnummer);

        SedType sedType = SedUtils.hentFørsteLovligeSedPåBuc(bucType);
        SedMapper sedMapper = SedMapperFactory.sedMapper(sedType);
        SED sed = sedMapper.mapTilSed(sedDataDto);

        OpprettBucOgSedResponse response = opprettEllerOppdaterBucOgSed(
                sed, vedlegg, bucType, gsakSaksnummer, sedDataDto.getMottakerIder()
        );

        if (sedDataDto.getBruker().isHarSensitiveOpplysninger()) {
            euxService.settSakSensitiv(response.getRinaSaksnummer());
        }

        if (sendAutomatisk) {
            sendSed(response.getRinaSaksnummer(), response.getDokumentId());
        }

        return OpprettSedDto.builder()
                .rinaSaksnummer(response.getRinaSaksnummer())
                .rinaUrl(euxService.hentRinaUrl(response.getRinaSaksnummer()))
                .build();
    }

    private void sendSed(String rinaSaksnummer, String dokumentId) throws IntegrationException {
        try {
            euxService.sendSed(rinaSaksnummer, dokumentId);
        } catch (IntegrationException e) {
            log.error("Feil ved oppretting og/eller sending av buc og sed. Exception fanges for å slette saksrelasjon.");
            euxService.slettBuC(rinaSaksnummer);
            saksrelasjonService.slettVedRinaId(rinaSaksnummer);
            throw e;
        }
    }

    public byte[] genererPdfFraSed(SedDataDto sedDataDto, SedType sedType) throws MappingException, NotFoundException, IntegrationException {
        SedMapper sedMapper = SedMapperFactory.sedMapper(sedType);
        SED sed = sedMapper.mapTilSed(sedDataDto);

        return euxService.genererPdfFraSed(sed);
    }

    public void sendPåEksisterendeBuc(SedDataDto sedDataDto, String rinaSaksnummer, SedType sedType) throws MappingException, NotFoundException, IntegrationException {
        BUC buc = euxService.hentBuc(rinaSaksnummer);
        if (!buc.kanOppretteSed(sedType)) {
            throw new IllegalArgumentException("Kan ikke opprette sed med type " + sedType + " på buc "+ rinaSaksnummer + " med type " + buc.getBucType());
        }

        SED sed = SedMapperFactory.sedMapper(sedType).mapTilSed(sedDataDto);
        euxService.opprettOgSendSed(sed, rinaSaksnummer);
    }

    private OpprettBucOgSedResponse opprettEllerOppdaterBucOgSed(SED sed, byte[] vedlegg, BucType bucType, Long gsakSaksnummer, List<String> mottakerIder) throws IntegrationException {
        SedType sedType = SedType.valueOf(sed.getSedType());

        if (sedType == SedType.A009) {
            Optional<BUC> eksisterendeSak = finnAapenEksisterendeSak(
                    saksrelasjonService.finnVedGsakSaksnummerOgBucType(gsakSaksnummer, bucType)
            );

            if (eksisterendeSak.isPresent()) {
                BUC buc = eksisterendeSak.get();
                Optional<Document> document = finnDokumentVedSedType(buc.getDocuments(), sed.getSedType());

                if (document.isPresent() && sedKanOppdateres(buc, document.get().getId())) {
                    String rinaSaksnummer = buc.getId();
                    String dokumentId = document.get().getId();
                    log.info("SED {} på rinasak {} oppdateres", dokumentId, rinaSaksnummer);
                    euxService.oppdaterSed(rinaSaksnummer, dokumentId, sed);
                    return new OpprettBucOgSedResponse(rinaSaksnummer, dokumentId);
                }
            }
        }

        return opprettOgLagreSaksrelasjon(sed, vedlegg, bucType, gsakSaksnummer, mottakerIder);
    }

    private static boolean sedKanOppdateres(BUC buc, String id) {
        return buc.getActions().stream().filter(action -> id.equals(action.getDocumentId()))
                .anyMatch(action -> "Update".equalsIgnoreCase(action.getOperation()));
    }

    private static Optional<Document> finnDokumentVedSedType(List<Document> documents, String sedType) {
        return documents.stream().filter(d -> sedType.equals(d.getType())).min(sorterEtterStatus());
    }

    private static Comparator<? super Document> sorterEtterStatus() {
        return Comparator.comparing(document -> SedStatus.fraEngelskStatus(document.getStatus()));
    }

    private Optional<BUC> finnAapenEksisterendeSak(List<FagsakRinasakKobling> eksisterendeSaker) throws IntegrationException {
        for (FagsakRinasakKobling fagsakRinasakKobling : eksisterendeSaker) {
            BUC buc = euxService.hentBuc(fagsakRinasakKobling.getRinaSaksnummer());
            if ("open".equals(buc.getStatus())) {
                return Optional.of(buc);
            }
        }

        return Optional.empty();
    }

    private OpprettBucOgSedResponse opprettOgLagreSaksrelasjon(SED sed, byte[] vedlegg, BucType bucType, Long gsakSaksnummer, List<String> mottakerIder)
            throws IntegrationException {
        OpprettBucOgSedResponse opprettBucOgSedResponse = euxService.opprettBucOgSed(bucType.name(), mottakerIder.get(0), sed, vedlegg); // TODO: skal sende inn liste av mottakerIder når eux støtter det.
        saksrelasjonService.lagreKobling(gsakSaksnummer, opprettBucOgSedResponse.getRinaSaksnummer(), bucType);
        log.info("gsakSaksnummer {} lagret med rinaId {}", gsakSaksnummer, opprettBucOgSedResponse.getRinaSaksnummer());
        return opprettBucOgSedResponse;
    }

    private static Long hentGsakSaksnummer(SedDataDto sedDataDto) throws MappingException {
        return Optional.ofNullable(sedDataDto.getGsakSaksnummer()).orElseThrow(() -> new MappingException("GsakId er påkrevd!"));
    }
}
