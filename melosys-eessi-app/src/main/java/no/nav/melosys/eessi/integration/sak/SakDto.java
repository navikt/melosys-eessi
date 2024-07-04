// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.integration.sak;

public class SakDto {
    private Long id;
    private String tema; // https://kodeverkviewer.adeo.no/kodeverk/xml/fagomrade.xml
    private String applikasjon; // Fagsystemkode for applikasjon
    private String fagsakNr; // Fagsaknr for den aktuelle saken
    private String aktoerId; // Id til aktøren saken gjelder
    private String orgnr; // Orgnr til foretaket saken gjelder
    private String opprettetAv;// Brukerident til den som opprettet saken
    private String opprettetTidspunkt; // Lagres som LocalDateTime i Sak API, men eksponeres som ZonedDateTime

    @java.lang.SuppressWarnings("all")
    public SakDto() {
    }

    @java.lang.SuppressWarnings("all")
    public Long getId() {
        return this.id;
    }

    @java.lang.SuppressWarnings("all")
    public String getTema() {
        return this.tema;
    }

    @java.lang.SuppressWarnings("all")
    public String getApplikasjon() {
        return this.applikasjon;
    }

    @java.lang.SuppressWarnings("all")
    public String getFagsakNr() {
        return this.fagsakNr;
    }

    @java.lang.SuppressWarnings("all")
    public String getAktoerId() {
        return this.aktoerId;
    }

    @java.lang.SuppressWarnings("all")
    public String getOrgnr() {
        return this.orgnr;
    }

    @java.lang.SuppressWarnings("all")
    public String getOpprettetAv() {
        return this.opprettetAv;
    }

    @java.lang.SuppressWarnings("all")
    public String getOpprettetTidspunkt() {
        return this.opprettetTidspunkt;
    }

    @java.lang.SuppressWarnings("all")
    public void setId(final Long id) {
        this.id = id;
    }

    @java.lang.SuppressWarnings("all")
    public void setTema(final String tema) {
        this.tema = tema;
    }

    @java.lang.SuppressWarnings("all")
    public void setApplikasjon(final String applikasjon) {
        this.applikasjon = applikasjon;
    }

    @java.lang.SuppressWarnings("all")
    public void setFagsakNr(final String fagsakNr) {
        this.fagsakNr = fagsakNr;
    }

    @java.lang.SuppressWarnings("all")
    public void setAktoerId(final String aktoerId) {
        this.aktoerId = aktoerId;
    }

    @java.lang.SuppressWarnings("all")
    public void setOrgnr(final String orgnr) {
        this.orgnr = orgnr;
    }

    @java.lang.SuppressWarnings("all")
    public void setOpprettetAv(final String opprettetAv) {
        this.opprettetAv = opprettetAv;
    }

    @java.lang.SuppressWarnings("all")
    public void setOpprettetTidspunkt(final String opprettetTidspunkt) {
        this.opprettetTidspunkt = opprettetTidspunkt;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof SakDto)) return false;
        final SakDto other = (SakDto) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$tema = this.getTema();
        final java.lang.Object other$tema = other.getTema();
        if (this$tema == null ? other$tema != null : !this$tema.equals(other$tema)) return false;
        final java.lang.Object this$applikasjon = this.getApplikasjon();
        final java.lang.Object other$applikasjon = other.getApplikasjon();
        if (this$applikasjon == null ? other$applikasjon != null : !this$applikasjon.equals(other$applikasjon))
            return false;
        final java.lang.Object this$fagsakNr = this.getFagsakNr();
        final java.lang.Object other$fagsakNr = other.getFagsakNr();
        if (this$fagsakNr == null ? other$fagsakNr != null : !this$fagsakNr.equals(other$fagsakNr)) return false;
        final java.lang.Object this$aktoerId = this.getAktoerId();
        final java.lang.Object other$aktoerId = other.getAktoerId();
        if (this$aktoerId == null ? other$aktoerId != null : !this$aktoerId.equals(other$aktoerId)) return false;
        final java.lang.Object this$orgnr = this.getOrgnr();
        final java.lang.Object other$orgnr = other.getOrgnr();
        if (this$orgnr == null ? other$orgnr != null : !this$orgnr.equals(other$orgnr)) return false;
        final java.lang.Object this$opprettetAv = this.getOpprettetAv();
        final java.lang.Object other$opprettetAv = other.getOpprettetAv();
        if (this$opprettetAv == null ? other$opprettetAv != null : !this$opprettetAv.equals(other$opprettetAv))
            return false;
        final java.lang.Object this$opprettetTidspunkt = this.getOpprettetTidspunkt();
        final java.lang.Object other$opprettetTidspunkt = other.getOpprettetTidspunkt();
        if (this$opprettetTidspunkt == null ? other$opprettetTidspunkt != null : !this$opprettetTidspunkt.equals(other$opprettetTidspunkt))
            return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof SakDto;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $tema = this.getTema();
        result = result * PRIME + ($tema == null ? 43 : $tema.hashCode());
        final java.lang.Object $applikasjon = this.getApplikasjon();
        result = result * PRIME + ($applikasjon == null ? 43 : $applikasjon.hashCode());
        final java.lang.Object $fagsakNr = this.getFagsakNr();
        result = result * PRIME + ($fagsakNr == null ? 43 : $fagsakNr.hashCode());
        final java.lang.Object $aktoerId = this.getAktoerId();
        result = result * PRIME + ($aktoerId == null ? 43 : $aktoerId.hashCode());
        final java.lang.Object $orgnr = this.getOrgnr();
        result = result * PRIME + ($orgnr == null ? 43 : $orgnr.hashCode());
        final java.lang.Object $opprettetAv = this.getOpprettetAv();
        result = result * PRIME + ($opprettetAv == null ? 43 : $opprettetAv.hashCode());
        final java.lang.Object $opprettetTidspunkt = this.getOpprettetTidspunkt();
        result = result * PRIME + ($opprettetTidspunkt == null ? 43 : $opprettetTidspunkt.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "SakDto(id=" + this.getId() + ", tema=" + this.getTema() + ", applikasjon=" + this.getApplikasjon() + ", fagsakNr=" + this.getFagsakNr() + ", aktoerId=" + this.getAktoerId() + ", orgnr=" + this.getOrgnr() + ", opprettetAv=" + this.getOpprettetAv() + ", opprettetTidspunkt=" + this.getOpprettetTidspunkt() + ")";
    }
}
