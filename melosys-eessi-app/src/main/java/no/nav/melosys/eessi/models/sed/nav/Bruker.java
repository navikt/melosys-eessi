// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.models.sed.nav;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Bruker {
    private List<Adresse> adresse;
    private Far far;
    private Mor mor;
    private Person person;

    @java.lang.SuppressWarnings("all")
    public Bruker() {
    }

    @java.lang.SuppressWarnings("all")
    public List<Adresse> getAdresse() {
        return this.adresse;
    }

    @java.lang.SuppressWarnings("all")
    public Far getFar() {
        return this.far;
    }

    @java.lang.SuppressWarnings("all")
    public Mor getMor() {
        return this.mor;
    }

    @java.lang.SuppressWarnings("all")
    public Person getPerson() {
        return this.person;
    }

    @java.lang.SuppressWarnings("all")
    public void setAdresse(final List<Adresse> adresse) {
        this.adresse = adresse;
    }

    @java.lang.SuppressWarnings("all")
    public void setFar(final Far far) {
        this.far = far;
    }

    @java.lang.SuppressWarnings("all")
    public void setMor(final Mor mor) {
        this.mor = mor;
    }

    @java.lang.SuppressWarnings("all")
    public void setPerson(final Person person) {
        this.person = person;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Bruker)) return false;
        final Bruker other = (Bruker) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$adresse = this.getAdresse();
        final java.lang.Object other$adresse = other.getAdresse();
        if (this$adresse == null ? other$adresse != null : !this$adresse.equals(other$adresse)) return false;
        final java.lang.Object this$far = this.getFar();
        final java.lang.Object other$far = other.getFar();
        if (this$far == null ? other$far != null : !this$far.equals(other$far)) return false;
        final java.lang.Object this$mor = this.getMor();
        final java.lang.Object other$mor = other.getMor();
        if (this$mor == null ? other$mor != null : !this$mor.equals(other$mor)) return false;
        final java.lang.Object this$person = this.getPerson();
        final java.lang.Object other$person = other.getPerson();
        if (this$person == null ? other$person != null : !this$person.equals(other$person)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Bruker;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $adresse = this.getAdresse();
        result = result * PRIME + ($adresse == null ? 43 : $adresse.hashCode());
        final java.lang.Object $far = this.getFar();
        result = result * PRIME + ($far == null ? 43 : $far.hashCode());
        final java.lang.Object $mor = this.getMor();
        result = result * PRIME + ($mor == null ? 43 : $mor.hashCode());
        final java.lang.Object $person = this.getPerson();
        result = result * PRIME + ($person == null ? 43 : $person.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "Bruker(adresse=" + this.getAdresse() + ", far=" + this.getFar() + ", mor=" + this.getMor() + ", person=" + this.getPerson() + ")";
    }
}
