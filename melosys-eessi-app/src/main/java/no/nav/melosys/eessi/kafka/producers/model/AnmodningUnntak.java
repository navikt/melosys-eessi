// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.kafka.producers.model;

public class AnmodningUnntak {
    private String unntakFraLovvalgsland;
    private String unntakFraLovvalgsbestemmelse;

    @java.lang.SuppressWarnings("all")
    public AnmodningUnntak() {
    }

    @java.lang.SuppressWarnings("all")
    public String getUnntakFraLovvalgsland() {
        return this.unntakFraLovvalgsland;
    }

    @java.lang.SuppressWarnings("all")
    public String getUnntakFraLovvalgsbestemmelse() {
        return this.unntakFraLovvalgsbestemmelse;
    }

    @java.lang.SuppressWarnings("all")
    public void setUnntakFraLovvalgsland(final String unntakFraLovvalgsland) {
        this.unntakFraLovvalgsland = unntakFraLovvalgsland;
    }

    @java.lang.SuppressWarnings("all")
    public void setUnntakFraLovvalgsbestemmelse(final String unntakFraLovvalgsbestemmelse) {
        this.unntakFraLovvalgsbestemmelse = unntakFraLovvalgsbestemmelse;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof AnmodningUnntak)) return false;
        final AnmodningUnntak other = (AnmodningUnntak) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$unntakFraLovvalgsland = this.getUnntakFraLovvalgsland();
        final java.lang.Object other$unntakFraLovvalgsland = other.getUnntakFraLovvalgsland();
        if (this$unntakFraLovvalgsland == null ? other$unntakFraLovvalgsland != null : !this$unntakFraLovvalgsland.equals(other$unntakFraLovvalgsland))
            return false;
        final java.lang.Object this$unntakFraLovvalgsbestemmelse = this.getUnntakFraLovvalgsbestemmelse();
        final java.lang.Object other$unntakFraLovvalgsbestemmelse = other.getUnntakFraLovvalgsbestemmelse();
        if (this$unntakFraLovvalgsbestemmelse == null ? other$unntakFraLovvalgsbestemmelse != null : !this$unntakFraLovvalgsbestemmelse.equals(other$unntakFraLovvalgsbestemmelse))
            return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof AnmodningUnntak;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $unntakFraLovvalgsland = this.getUnntakFraLovvalgsland();
        result = result * PRIME + ($unntakFraLovvalgsland == null ? 43 : $unntakFraLovvalgsland.hashCode());
        final java.lang.Object $unntakFraLovvalgsbestemmelse = this.getUnntakFraLovvalgsbestemmelse();
        result = result * PRIME + ($unntakFraLovvalgsbestemmelse == null ? 43 : $unntakFraLovvalgsbestemmelse.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "AnmodningUnntak(unntakFraLovvalgsland=" + this.getUnntakFraLovvalgsland() + ", unntakFraLovvalgsbestemmelse=" + this.getUnntakFraLovvalgsbestemmelse() + ")";
    }
}
