// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.models.sed.medlemskap.impl;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.models.sed.nav.Andreland;
import no.nav.melosys.eessi.models.sed.nav.VedtakA003;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MedlemskapA003 implements Medlemskap {
    private List<String> gjeldendereglerEC883;
    private String relevantartikkelfor8832004eller9872009;
    private Andreland andreland;
    private VedtakA003 vedtak;
    private String isDeterminationProvisional;

    @java.lang.SuppressWarnings("all")
    public MedlemskapA003() {
    }

    @java.lang.SuppressWarnings("all")
    public List<String> getGjeldendereglerEC883() {
        return this.gjeldendereglerEC883;
    }

    @java.lang.SuppressWarnings("all")
    public String getRelevantartikkelfor8832004eller9872009() {
        return this.relevantartikkelfor8832004eller9872009;
    }

    @java.lang.SuppressWarnings("all")
    public Andreland getAndreland() {
        return this.andreland;
    }

    @java.lang.SuppressWarnings("all")
    public VedtakA003 getVedtak() {
        return this.vedtak;
    }

    @java.lang.SuppressWarnings("all")
    public String getIsDeterminationProvisional() {
        return this.isDeterminationProvisional;
    }

    @java.lang.SuppressWarnings("all")
    public void setGjeldendereglerEC883(final List<String> gjeldendereglerEC883) {
        this.gjeldendereglerEC883 = gjeldendereglerEC883;
    }

    @java.lang.SuppressWarnings("all")
    public void setRelevantartikkelfor8832004eller9872009(final String relevantartikkelfor8832004eller9872009) {
        this.relevantartikkelfor8832004eller9872009 = relevantartikkelfor8832004eller9872009;
    }

    @java.lang.SuppressWarnings("all")
    public void setAndreland(final Andreland andreland) {
        this.andreland = andreland;
    }

    @java.lang.SuppressWarnings("all")
    public void setVedtak(final VedtakA003 vedtak) {
        this.vedtak = vedtak;
    }

    @java.lang.SuppressWarnings("all")
    public void setIsDeterminationProvisional(final String isDeterminationProvisional) {
        this.isDeterminationProvisional = isDeterminationProvisional;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof MedlemskapA003)) return false;
        final MedlemskapA003 other = (MedlemskapA003) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$gjeldendereglerEC883 = this.getGjeldendereglerEC883();
        final java.lang.Object other$gjeldendereglerEC883 = other.getGjeldendereglerEC883();
        if (this$gjeldendereglerEC883 == null ? other$gjeldendereglerEC883 != null : !this$gjeldendereglerEC883.equals(other$gjeldendereglerEC883)) return false;
        final java.lang.Object this$relevantartikkelfor8832004eller9872009 = this.getRelevantartikkelfor8832004eller9872009();
        final java.lang.Object other$relevantartikkelfor8832004eller9872009 = other.getRelevantartikkelfor8832004eller9872009();
        if (this$relevantartikkelfor8832004eller9872009 == null ? other$relevantartikkelfor8832004eller9872009 != null : !this$relevantartikkelfor8832004eller9872009.equals(other$relevantartikkelfor8832004eller9872009)) return false;
        final java.lang.Object this$andreland = this.getAndreland();
        final java.lang.Object other$andreland = other.getAndreland();
        if (this$andreland == null ? other$andreland != null : !this$andreland.equals(other$andreland)) return false;
        final java.lang.Object this$vedtak = this.getVedtak();
        final java.lang.Object other$vedtak = other.getVedtak();
        if (this$vedtak == null ? other$vedtak != null : !this$vedtak.equals(other$vedtak)) return false;
        final java.lang.Object this$isDeterminationProvisional = this.getIsDeterminationProvisional();
        final java.lang.Object other$isDeterminationProvisional = other.getIsDeterminationProvisional();
        if (this$isDeterminationProvisional == null ? other$isDeterminationProvisional != null : !this$isDeterminationProvisional.equals(other$isDeterminationProvisional)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof MedlemskapA003;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $gjeldendereglerEC883 = this.getGjeldendereglerEC883();
        result = result * PRIME + ($gjeldendereglerEC883 == null ? 43 : $gjeldendereglerEC883.hashCode());
        final java.lang.Object $relevantartikkelfor8832004eller9872009 = this.getRelevantartikkelfor8832004eller9872009();
        result = result * PRIME + ($relevantartikkelfor8832004eller9872009 == null ? 43 : $relevantartikkelfor8832004eller9872009.hashCode());
        final java.lang.Object $andreland = this.getAndreland();
        result = result * PRIME + ($andreland == null ? 43 : $andreland.hashCode());
        final java.lang.Object $vedtak = this.getVedtak();
        result = result * PRIME + ($vedtak == null ? 43 : $vedtak.hashCode());
        final java.lang.Object $isDeterminationProvisional = this.getIsDeterminationProvisional();
        result = result * PRIME + ($isDeterminationProvisional == null ? 43 : $isDeterminationProvisional.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "MedlemskapA003(gjeldendereglerEC883=" + this.getGjeldendereglerEC883() + ", relevantartikkelfor8832004eller9872009=" + this.getRelevantartikkelfor8832004eller9872009() + ", andreland=" + this.getAndreland() + ", vedtak=" + this.getVedtak() + ", isDeterminationProvisional=" + this.getIsDeterminationProvisional() + ")";
    }
}
