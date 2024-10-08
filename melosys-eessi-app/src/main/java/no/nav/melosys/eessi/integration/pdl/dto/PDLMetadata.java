// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.integration.pdl.dto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

public class PDLMetadata {
    private String opplysningsId;
    private String master;
    private Collection<PDLEndring> endringer = Collections.emptyList();
    private boolean historisk;

    public LocalDateTime sisteDatoOpprettetEllerKorrigert() {
        return endringer.stream().filter(PDLEndring::erOpprettelseEllerKorrigering).map(PDLEndring::getRegistrert).max(LocalDateTime::compareTo).orElse(LocalDateTime.MIN);
    }

    @java.lang.SuppressWarnings("all")
    public PDLMetadata() {
    }

    @java.lang.SuppressWarnings("all")
    public String getOpplysningsId() {
        return this.opplysningsId;
    }

    @java.lang.SuppressWarnings("all")
    public String getMaster() {
        return this.master;
    }

    @java.lang.SuppressWarnings("all")
    public Collection<PDLEndring> getEndringer() {
        return this.endringer;
    }

    @java.lang.SuppressWarnings("all")
    public boolean isHistorisk() {
        return this.historisk;
    }

    @java.lang.SuppressWarnings("all")
    public void setOpplysningsId(final String opplysningsId) {
        this.opplysningsId = opplysningsId;
    }

    @java.lang.SuppressWarnings("all")
    public void setMaster(final String master) {
        this.master = master;
    }

    @java.lang.SuppressWarnings("all")
    public void setEndringer(final Collection<PDLEndring> endringer) {
        this.endringer = endringer;
    }

    @java.lang.SuppressWarnings("all")
    public void setHistorisk(final boolean historisk) {
        this.historisk = historisk;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof PDLMetadata)) return false;
        final PDLMetadata other = (PDLMetadata) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        if (this.isHistorisk() != other.isHistorisk()) return false;
        final java.lang.Object this$opplysningsId = this.getOpplysningsId();
        final java.lang.Object other$opplysningsId = other.getOpplysningsId();
        if (this$opplysningsId == null ? other$opplysningsId != null : !this$opplysningsId.equals(other$opplysningsId))
            return false;
        final java.lang.Object this$master = this.getMaster();
        final java.lang.Object other$master = other.getMaster();
        if (this$master == null ? other$master != null : !this$master.equals(other$master)) return false;
        final java.lang.Object this$endringer = this.getEndringer();
        final java.lang.Object other$endringer = other.getEndringer();
        if (this$endringer == null ? other$endringer != null : !this$endringer.equals(other$endringer)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof PDLMetadata;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isHistorisk() ? 79 : 97);
        final java.lang.Object $opplysningsId = this.getOpplysningsId();
        result = result * PRIME + ($opplysningsId == null ? 43 : $opplysningsId.hashCode());
        final java.lang.Object $master = this.getMaster();
        result = result * PRIME + ($master == null ? 43 : $master.hashCode());
        final java.lang.Object $endringer = this.getEndringer();
        result = result * PRIME + ($endringer == null ? 43 : $endringer.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "PDLMetadata(opplysningsId=" + this.getOpplysningsId() + ", master=" + this.getMaster() + ", endringer=" + this.getEndringer() + ", historisk=" + this.isHistorisk() + ")";
    }
}
