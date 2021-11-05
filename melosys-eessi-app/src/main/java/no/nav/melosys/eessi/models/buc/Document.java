package no.nav.melosys.eessi.models.buc;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import no.nav.melosys.eessi.controller.dto.SedStatus;
import no.nav.melosys.eessi.models.SedType;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Document {

    private String id;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime creationDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime lastUpdate;
    private Creator creator;
    private String type;
    private String status;
    private String direction;
    private List<Conversation> conversations = new ArrayList<>();

    public boolean sedErSendt() {
        return !getConversations().isEmpty()
                && getConversations().get(0).getVersionId() != null
                && SedType.erLovvalgSed(type);
    }

    public boolean erOpprettet() {
        return !SedStatus.TOM.getEngelskStatus().equalsIgnoreCase(status);
    }

    public boolean erX001() {
        return SedType.X001.name().equals(type);
    }

    public boolean erIkkeX100() {
        return !SedType.X100.name().equals(type);
    }

    public boolean erLovvalgSED() {
        return SedType.erLovvalgSed(type);
    }

    public boolean erAntallDagerSidenOppdatering(long antallDagerSidenOppdatert) {
        return ZonedDateTime.now().minusDays(antallDagerSidenOppdatert).isAfter(getLastUpdate());
    }

    public boolean erInngående() {
        return "IN".equalsIgnoreCase(direction);
    }
}
