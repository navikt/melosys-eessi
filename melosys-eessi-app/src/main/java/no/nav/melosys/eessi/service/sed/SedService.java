package no.nav.melosys.eessi.service.sed;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.OpprettSedDto;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.controller.dto.SvarAnmodningUnntakDto;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Document;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.SvarAnmodningUnntakBeslutning;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.eux.OpprettBucOgSedResponse;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import no.nav.melosys.eessi.service.sed.helpers.SedMapperFactory;
import no.nav.melosys.eessi.service.sed.mapper.SedMapper;
import no.nav.melosys.eessi.service.sed.mapper.lovvalg.A002Mapper;
import no.nav.melosys.eessi.service.sed.mapper.lovvalg.A011Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SedService {

    private final EuxService euxService;
    private final SaksrelasjonService saksrelasjonService;

    @Autowired
    public SedService(EuxService euxService,
                      SaksrelasjonService saksrelasjonService) {
        this.euxService = euxService;
        this.saksrelasjonService = saksrelasjonService;
    }

    public String createAndSend(SedDataDto sedDataDto) throws MappingException, IntegrationException, NotFoundException {

        Long gsakSaksnummer = getGsakSaksnummer(sedDataDto);

        log.info("Oppretter buc og sed, gsakSaksnummer: {}", gsakSaksnummer);
        BucType bucType = SedUtils.getBucTypeFromBestemmelse(
                sedDataDto.getLovvalgsperioder().get(0).getBestemmelse());
        SedType sedType = SedUtils.hentFoersteLovligeSedPaaBuc(bucType);
        SedMapper sedMapper = SedMapperFactory.sedMapper(sedType);

        SED sed = sedMapper.mapTilSed(sedDataDto);

        OpprettBucOgSedResponse opprettBucOgSedResponse =
                opprettEllerOppdaterBucOgSed(bucType, sed, gsakSaksnummer, sedDataDto.getMottakerLand(), sedDataDto.getMottakerId());

        try {
            euxService.sendSed(opprettBucOgSedResponse.getRinaSaksnummer(), opprettBucOgSedResponse.getDokumentId());
        } catch (IntegrationException e) {
            log.error("Feil ved oppretting og/eller sending av buc og sed. Exception fanges for å slette saksrelasjon.");
            euxService.slettBuC(opprettBucOgSedResponse.getRinaSaksnummer());
            saksrelasjonService.slettVedRinaId(opprettBucOgSedResponse.getRinaSaksnummer());
            throw e;
        }

        return opprettBucOgSedResponse.getRinaSaksnummer();
    }

    /**
     * Oppretter en SED på en ny BUC.
     *
     * @param sedDataDto sed som skal opprettes
     * @param bucType    hvilken type buc som skal opprettes (dersom det ikke er en eksisterende buc på saken)
     * @return Dto-objekt som inneholder rinaSaksnummer, dokumentId og link til sak i rina
     */
    public OpprettSedDto createSed(SedDataDto sedDataDto, BucType bucType)
            throws MappingException, NotFoundException, IntegrationException {

        SedType sedType = SedUtils.hentFoersteLovligeSedPaaBuc(bucType);

        Long gsakSaksnummer = getGsakSaksnummer(sedDataDto);
        SedMapper sedMapper = SedMapperFactory.sedMapper(sedType);
        SED sed = sedMapper.mapTilSed(sedDataDto);

        log.info("Oppretter buc og sed, gsakSaksnummer: {}", gsakSaksnummer);
        OpprettBucOgSedResponse opprettBucOgSedResponse =
                opprettOgLagreSaksrelasjon(bucType, sed, gsakSaksnummer, sedDataDto.getMottakerLand(), sedDataDto.getMottakerId());

        return OpprettSedDto.builder()
                .bucId(opprettBucOgSedResponse.getRinaSaksnummer())
                .rinaUrl(euxService.hentRinaUrl(opprettBucOgSedResponse.getRinaSaksnummer()))
                .build();
    }

    public void anmodningUnntakSvar(SvarAnmodningUnntakDto svarAnmodningUnntakDto, String rinaId) throws IntegrationException, NotFoundException {
        SED nySed;
        SED forrigeSed = hentA001ForBuc(rinaId);
        if (svarAnmodningUnntakDto.getBeslutning() == SvarAnmodningUnntakBeslutning.INNVILGELSE) {
            nySed = new A011Mapper().mapFraSed(forrigeSed);
        } else {
            String begrunnelse = svarAnmodningUnntakDto.getBegrunnelse();
            SvarAnmodningUnntakBeslutning beslutning = svarAnmodningUnntakDto.getBeslutning();
            LocalDate fom = svarAnmodningUnntakDto.getDelvisInnvilgetPeriode().getFom();
            LocalDate tom = svarAnmodningUnntakDto.getDelvisInnvilgetPeriode().getTom();
            nySed = new A002Mapper().mapFraSed(forrigeSed, begrunnelse, beslutning, fom, tom);
        }

        log.info("Sender svar på anmodning om unntak for rinasak {}", rinaId);
        euxService.opprettOgSendSed(nySed, rinaId);
    }

    private SED hentA001ForBuc(String rinaId) throws IntegrationException, NotFoundException {
        return euxService.hentSed(rinaId, hentA001Document(rinaId).getId());
    }

    private Document hentA001Document(String rinaId) throws IntegrationException, NotFoundException {
        return euxService.hentBuc(rinaId).getDocuments().stream()
                .filter(document -> SedType.A001.toString().equalsIgnoreCase(document.getType()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Finner ingen A001 for BUC " + rinaId));
    }

    private OpprettBucOgSedResponse opprettEllerOppdaterBucOgSed(BucType bucType, SED sed, Long gsakSaksnummer, String mottakerLand, String mottakerId) throws NotFoundException, IntegrationException {
        SedType sedType = SedType.valueOf(sed.getSed());

        if (sedType == SedType.A009) {
            Optional<BUC> eksisterendeSak = finnAapenEksisterendeSak(
                    saksrelasjonService.finnVedGsakSaksnummerOgBucType(gsakSaksnummer, bucType)
            );

            if (eksisterendeSak.isPresent()) {
                BUC buc = eksisterendeSak.get();
                Optional<Document> document = finnDokumentVedSedType(buc.getDocuments(), sed.getSed());

                if (document.isPresent() && sedKanOppdateres(buc, document.get().getId())) {
                    String rinaSaksnummer = buc.getId();
                    String dokumentId = document.get().getId();
                    log.info("SED {} på rinasak {} oppdateres", dokumentId, rinaSaksnummer);
                    euxService.oppdaterSed(rinaSaksnummer, dokumentId, sed);
                    return new OpprettBucOgSedResponse(rinaSaksnummer, dokumentId);
                }
            }
        }

        return opprettOgLagreSaksrelasjon(bucType, sed, gsakSaksnummer, mottakerLand, mottakerId);
    }

    private static boolean sedKanOppdateres(BUC buc, String id) {
        return buc.getActions().stream().filter(action -> id.equals(action.getDocumentId()))
                .anyMatch(action -> "Update".equalsIgnoreCase(action.getOperation()));
    }

    private static Optional<Document> finnDokumentVedSedType(List<Document> documents, String sedType) {
        return documents.stream().filter(d -> sedType.equals(d.getType())).findFirst();
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

    private OpprettBucOgSedResponse opprettOgLagreSaksrelasjon(BucType bucType, SED sed, Long gsakSaksnummer, String mottakerLand, String mottakerId)
            throws NotFoundException, IntegrationException {
        OpprettBucOgSedResponse opprettBucOgSedResponse = euxService.opprettBucOgSed(bucType.name(), mottakerLand, mottakerId, sed);
        saksrelasjonService.lagreKobling(gsakSaksnummer, opprettBucOgSedResponse.getRinaSaksnummer(), bucType);
        log.info("gsakSaksnummer {} lagret med rinaId {}", gsakSaksnummer, opprettBucOgSedResponse.getRinaSaksnummer());
        return opprettBucOgSedResponse;
    }

    private static Long getGsakSaksnummer(SedDataDto sedDataDto) throws MappingException {
        return Optional.ofNullable(sedDataDto.getGsakSaksnummer()).orElseThrow(() -> new MappingException("GsakId er påkrevd!"));
    }
}
