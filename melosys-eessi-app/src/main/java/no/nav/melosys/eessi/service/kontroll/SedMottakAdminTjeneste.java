package no.nav.melosys.eessi.service.kontroll;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.models.SedMottattHendelseDto;
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository;
import no.nav.melosys.eessi.service.mottak.SedMottakService;
import no.nav.security.token.support.core.api.Unprotected;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/sedmottak")
@Slf4j
public class SedMottakAdminTjeneste {

    private final static String API_KEY_HEADER = "X-MELOSYS-ADMIN-APIKEY";

    private final SedMottakService sedMottakService;
    private final SedMottattHendelseRepository sedMottattHendelseRepository;
    private final String apiKey;

    public SedMottakAdminTjeneste(SedMottakService sedMottakService,
                                  SedMottattHendelseRepository sedMottattHendelseRepository,
                                  @Value("${melosys.admin.api-key}") String apiKey) {
        this.sedMottakService = sedMottakService;
        this.sedMottattHendelseRepository = sedMottattHendelseRepository;
        this.apiKey = apiKey;
    }

    @GetMapping("/feilede")
    public ResponseEntity<Collection<SedMottattHendelseDto>> hentSEDerMottattUtenJournalpostId(
        @RequestHeader(API_KEY_HEADER) String apiKey) {

        validerApikey(apiKey);
        return ResponseEntity.ok(lagSedMottattHendelseDtoer(hentAlleSEDerUtenJournalpostID()));
    }

    @PostMapping("/feilede/restart")
    public ResponseEntity<Collection<SedMottattHendelseDto>> restartAlleSEDerUtenJournalpostId(
        @RequestHeader(API_KEY_HEADER) String apiKey) {
        validerApikey(apiKey);
        Collection<SedMottattHendelse> sedUtenJournalpost = hentAlleSEDerUtenJournalpostID();
        log.info("Forsøker å restarte feilede SEDer ");
        restartAlleFeiledeSEDer(sedUtenJournalpost);
        return ResponseEntity.ok(lagSedMottattHendelseDtoer(sedUtenJournalpost)
        );
    }

    private List<SedMottattHendelse> hentAlleSEDerUtenJournalpostID() {
        return new ArrayList<>(sedMottattHendelseRepository
            .findAllByJournalpostIdIsNullOrderByMottattDato());
    }

    private Collection<SedMottattHendelseDto> lagSedMottattHendelseDtoer(Collection<SedMottattHendelse> sedMottattHendelser) {
        return sedMottattHendelser
            .stream()
            .map(this::lagSedMottattHendelseDto)
            .collect(Collectors.toList());
    }

    private SedMottattHendelseDto lagSedMottattHendelseDto(SedMottattHendelse sedMottattHendelse) {
        return new SedMottattHendelseDto(
            sedMottattHendelse.getId(),
            sedMottattHendelse.getSedHendelse(),
            sedMottattHendelse.getJournalpostId(),
            sedMottattHendelse.isPublisertKafka(),
            sedMottattHendelse.getMottattDato(),
            sedMottattHendelse.getSistEndretDato());
    }

    private void restartAlleFeiledeSEDer(Collection<SedMottattHendelse> sedmottattHendelser) {
        sedmottattHendelser
            .forEach(sedMottakService::behandleSedMottakHendelse);
    }

    private void validerApikey(String value) {
        if (!getApiKey().equals(value)) {
            throw new SecurityException("Trenger gyldig apikey");
        }
    }

    private String getApiKey() {
        return apiKey;
    }
}
