// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.models.sed.nav;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Andreland {
    private List<Arbeidsgiver> arbeidsgiver;
    //A003
    private ArbeidsgiverAktivitet arbeidsgiveraktivitet;

    @java.lang.SuppressWarnings("all")
    public Andreland() {
    }

    @java.lang.SuppressWarnings("all")
    public List<Arbeidsgiver> getArbeidsgiver() {
        return this.arbeidsgiver;
    }

    @java.lang.SuppressWarnings("all")
    public ArbeidsgiverAktivitet getArbeidsgiveraktivitet() {
        return this.arbeidsgiveraktivitet;
    }

    @java.lang.SuppressWarnings("all")
    public void setArbeidsgiver(final List<Arbeidsgiver> arbeidsgiver) {
        this.arbeidsgiver = arbeidsgiver;
    }

    @java.lang.SuppressWarnings("all")
    public void setArbeidsgiveraktivitet(final ArbeidsgiverAktivitet arbeidsgiveraktivitet) {
        this.arbeidsgiveraktivitet = arbeidsgiveraktivitet;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Andreland)) return false;
        final Andreland other = (Andreland) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$arbeidsgiver = this.getArbeidsgiver();
        final java.lang.Object other$arbeidsgiver = other.getArbeidsgiver();
        if (this$arbeidsgiver == null ? other$arbeidsgiver != null : !this$arbeidsgiver.equals(other$arbeidsgiver))
            return false;
        final java.lang.Object this$arbeidsgiveraktivitet = this.getArbeidsgiveraktivitet();
        final java.lang.Object other$arbeidsgiveraktivitet = other.getArbeidsgiveraktivitet();
        if (this$arbeidsgiveraktivitet == null ? other$arbeidsgiveraktivitet != null : !this$arbeidsgiveraktivitet.equals(other$arbeidsgiveraktivitet))
            return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Andreland;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $arbeidsgiver = this.getArbeidsgiver();
        result = result * PRIME + ($arbeidsgiver == null ? 43 : $arbeidsgiver.hashCode());
        final java.lang.Object $arbeidsgiveraktivitet = this.getArbeidsgiveraktivitet();
        result = result * PRIME + ($arbeidsgiveraktivitet == null ? 43 : $arbeidsgiveraktivitet.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "Andreland(arbeidsgiver=" + this.getArbeidsgiver() + ", arbeidsgiveraktivitet=" + this.getArbeidsgiveraktivitet() + ")";
    }
}
