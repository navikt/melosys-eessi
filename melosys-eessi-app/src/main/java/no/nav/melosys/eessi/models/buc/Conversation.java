// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.models.buc;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Conversation {
    private String id;
    private String versionId;
    private List<Participant> participants = new ArrayList<>();

    @java.lang.SuppressWarnings("all")
    public Conversation() {
    }

    @java.lang.SuppressWarnings("all")
    public String getId() {
        return this.id;
    }

    @java.lang.SuppressWarnings("all")
    public String getVersionId() {
        return this.versionId;
    }

    @java.lang.SuppressWarnings("all")
    public List<Participant> getParticipants() {
        return this.participants;
    }

    @java.lang.SuppressWarnings("all")
    public void setId(final String id) {
        this.id = id;
    }

    @java.lang.SuppressWarnings("all")
    public void setVersionId(final String versionId) {
        this.versionId = versionId;
    }

    @java.lang.SuppressWarnings("all")
    public void setParticipants(final List<Participant> participants) {
        this.participants = participants;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Conversation)) return false;
        final Conversation other = (Conversation) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$versionId = this.getVersionId();
        final java.lang.Object other$versionId = other.getVersionId();
        if (this$versionId == null ? other$versionId != null : !this$versionId.equals(other$versionId)) return false;
        final java.lang.Object this$participants = this.getParticipants();
        final java.lang.Object other$participants = other.getParticipants();
        if (this$participants == null ? other$participants != null : !this$participants.equals(other$participants)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Conversation;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $versionId = this.getVersionId();
        result = result * PRIME + ($versionId == null ? 43 : $versionId.hashCode());
        final java.lang.Object $participants = this.getParticipants();
        result = result * PRIME + ($participants == null ? 43 : $participants.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "Conversation(id=" + this.getId() + ", versionId=" + this.getVersionId() + ", participants=" + this.getParticipants() + ")";
    }
}
