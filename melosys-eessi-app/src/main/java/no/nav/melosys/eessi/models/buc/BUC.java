package no.nav.melosys.eessi.models.buc;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import no.nav.melosys.eessi.controller.dto.SedStatus;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BUC {

    private String id;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime lastUpdate;
    private String status;
    private Creator creator;
    private List<Document> documents;
    private List<Action> actions;
    @JsonProperty(value = "processDefinitionName")
    private String bucType;
    @JsonProperty(value = "processDefinitionVersion")
    private String bucVersjon;

    public boolean kanOppretteEllerOppdatereSed(SedType sedType) {
        return actions.stream().anyMatch(action ->
                sedType.name().equalsIgnoreCase(action.getDocumentType())
                        && ("CREATE".equalsIgnoreCase(action.getOperation())) || "UPDATE".equalsIgnoreCase(action.getOperation()));
    }

    public Document hentDokument(String dokumentID) {
        return documents.stream().filter(d -> d.getId().equalsIgnoreCase(dokumentID)).findAny().orElseThrow();
    }

    public Optional<Document> hentSistOppdaterteDocument() {
        return documents.stream().filter(d -> SedStatus.erGyldigEngelskStatus(d.getStatus())).max(sistOppdatert);
    }

    public boolean erÅpen() {
        return !"closed".equalsIgnoreCase(status);
    }


    public Optional<Document> finnDokumentVedSedType(String sedType) {
        return finnDokumenterVedSedType(sedType).min(sorterEtterStatus);
    }

    private Stream<Document> finnDokumenterVedSedType(String sedType) {
        return documents.stream().filter(d -> sedType.equals(d.getType()));
    }

    private static final Comparator<Document> sistOppdatert = Comparator.comparing(Document::getLastUpdate);

    private static final Comparator<Document> sorterEtterStatus = Comparator.comparing(document -> SedStatus.fraEngelskStatus(document.getStatus()));

    public Optional<Document> finnDokumentVedTypeOgStatus(SedType sedType, SedStatus status) {
        return finnDokumenterVedSedType(sedType.name())
                .filter(d -> status.getEngelskStatus().equals(d.getStatus()))
                .findFirst();
    }

    public boolean sedKanOppdateres(String id) {
        return actions.stream()
                .filter(action -> id.equals(action.getDocumentId()))
                .anyMatch(action -> "Update".equalsIgnoreCase(action.getOperation()));
    }

    public boolean harMottattSedTypeAntallDagerSiden(SedType sedType, long minstAntallDagerSidenMottatt) {
        return finnDokumentVedTypeOgStatus(sedType, SedStatus.MOTTATT)
                .filter(d -> ZonedDateTime.now().minusDays(minstAntallDagerSidenMottatt).isAfter(d.getLastUpdate()))
                .isPresent();
    }

    public boolean kanLukkes() {
        var bucType = BucType.valueOf(getBucType());

        if (bucType == BucType.LA_BUC_06) {
            return harMottattSedTypeAntallDagerSiden(SedType.A006, 30)
                    && kanOppretteEllerOppdatereSed(SedType.X001);
        }

        return kanOppretteEllerOppdatereSed(SedType.X001);
    }
}
