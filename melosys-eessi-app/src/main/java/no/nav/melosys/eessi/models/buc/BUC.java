package no.nav.melosys.eessi.models.buc;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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
    private List<Document> documents = new ArrayList<>();
    private List<Action> actions = new ArrayList<>();
    @JsonProperty(value = "processDefinitionName")
    private String bucType;
    @JsonProperty(value = "processDefinitionVersion")
    private String bucVersjon;
    private Collection<Participant> participants = new ArrayList<>();
    private String internationalId;

    private static final Comparator<Document> sistOppdatert = Comparator.comparing(Document::getLastUpdate);
    private static final Comparator<Document> sorterEtterStatus = Comparator.comparing(document -> SedStatus.fraEngelskStatus(document.getStatus()));

    public String hentAvsenderLand() {
        return creator.getOrganisation().getCountryCode();
    }

    public boolean kanOppretteEllerOppdatereSed(SedType sedType) {
        return actions.stream()
            .filter(a -> sedType.name().equalsIgnoreCase(a.getDocumentType()))
            .anyMatch(action -> "CREATE".equalsIgnoreCase(action.getOperation()) || "UPDATE".equalsIgnoreCase(action.getOperation()));
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
            .filter(d -> d.erAntallDagerSidenOppdatering(minstAntallDagerSidenMottatt))
            .isPresent();
    }

    public boolean harSendtSedTypeAntallDagerSiden(SedType sedType, long minstAntallDagerSidenMottatt) {
        return finnDokumentVedTypeOgStatus(sedType, SedStatus.SENDT)
            .filter(d -> d.erAntallDagerSidenOppdatering(minstAntallDagerSidenMottatt))
            .isPresent();
    }

    public boolean kanLukkesAutomatisk() {
        var bucTypeEnum = BucType.valueOf(getBucType());

        if (bucTypeEnum == BucType.LA_BUC_06) {
            return harMottattSedTypeAntallDagerSiden(SedType.A006, 30)
                && kanOppretteEllerOppdatereSed(SedType.X001);
        } else if (bucTypeEnum == BucType.LA_BUC_01) {
            boolean harMottattA002EllerA011 = harMottattSedTypeAntallDagerSiden(SedType.A002, 60)
                || harMottattSedTypeAntallDagerSiden(SedType.A011, 60);

            return harMottattA002EllerA011
                && kanOppretteEllerOppdatereSed(SedType.X001)
                && finnSistMottattSED(Document::erLovvalgSED)
                .map(d -> d.erAntallDagerSidenOppdatering(60)).orElse(false);
        } else if (bucTypeEnum == BucType.LA_BUC_03) {
            boolean harMottattX012EllerSendtX013EllerA008 = (finnDokumentVedTypeOgStatus(SedType.X012, SedStatus.MOTTATT).isEmpty() || harMottattSedTypeAntallDagerSiden(SedType.X012, 30))
                && (finnDokumentVedTypeOgStatus(SedType.X013, SedStatus.SENDT).isEmpty() || harSendtSedTypeAntallDagerSiden(SedType.X013, 30))
                && harSendtSedTypeAntallDagerSiden(SedType.A008, 30);

            return harMottattX012EllerSendtX013EllerA008
                && kanOppretteEllerOppdatereSed(SedType.X001);

        }

        return kanOppretteEllerOppdatereSed(SedType.X001);
    }

    private Optional<Document> finnSistMottattSED(Predicate<Document> documentPredicate) {
        return documents.stream()
            .filter(Document::erInngående)
            .filter(Document::erOpprettet)
            .filter(documentPredicate)
            .max(Comparator.comparing(Document::getLastUpdate));
    }

    public Optional<Document> finnFørstMottatteSed() {
        return documents.stream()
            .filter(Document::erInngående)
            .filter(Document::erOpprettet)
            .min(Comparator.comparing(Document::getCreationDate));
    }

    // Fjernes sammen med toggle melosys.eessi.x100
    public Optional<Document> finnFørstMottatteSedIkkeX100() {
        return documents.stream()
            .filter(Document::erInngående)
            .filter(Document::erOpprettet)
            .filter(Document::erIkkeX100)
            .min(Comparator.comparing(Document::getCreationDate));
    }

    public Set<String> hentMottakere() {
        return participants.stream()
            .filter(Participant::erMotpart)
            .map(p -> p.getOrganisation().getId())
            .collect(Collectors.toSet());
    }
}
