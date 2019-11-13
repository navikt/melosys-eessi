package no.nav.melosys.eessi.models.buc;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import no.nav.melosys.eessi.controller.dto.SedStatus;
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

    public boolean kanOppretteSed(SedType sedType) {
        return actions.stream().anyMatch(action ->
                sedType.name().equalsIgnoreCase(action.getDocumentType()) && "CREATE".equalsIgnoreCase(action.getOperation()));
    }

    public Optional<Document> hentSistOppdaterteDocument() {
        return documents.stream().filter(d -> SedStatus.erGyldigEngelskStatus(d.getStatus())).max(sistOppdatert);
    }

    private static final Comparator<Document> sistOppdatert = Comparator.comparing(Document::getLastUpdate);
}
