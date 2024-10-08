// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.kafka.producers.model;

import java.util.ArrayList;
import java.util.List;

public class MelosysEessiMelding {
    private String sedId;
    private String sedVersjon;
    private String rinaSaksnummer;
    private Avsender avsender;
    private String journalpostId;
    private String dokumentId;
    private Long gsakSaksnummer;
    private String aktoerId;
    private List<Statsborgerskap> statsborgerskap = new ArrayList<>();
    private List<Arbeidssted> arbeidssteder = new ArrayList<>();
    private List<Arbeidsland> arbeidsland = new ArrayList<>();
    private Periode periode;
    private String lovvalgsland;
    private String artikkel;
    private boolean erEndring;
    private boolean midlertidigBestemmelse;
    private boolean x006NavErFjernet;
    private String ytterligereInformasjon;
    private String bucType;
    private String sedType;
    private SvarAnmodningUnntak svarAnmodningUnntak;
    private AnmodningUnntak anmodningUnntak;

    @java.lang.SuppressWarnings("all")
    public MelosysEessiMelding() {
    }

    @java.lang.SuppressWarnings("all")
    public String getSedId() {
        return this.sedId;
    }

    @java.lang.SuppressWarnings("all")
    public String getSedVersjon() {
        return this.sedVersjon;
    }

    @java.lang.SuppressWarnings("all")
    public String getRinaSaksnummer() {
        return this.rinaSaksnummer;
    }

    @java.lang.SuppressWarnings("all")
    public Avsender getAvsender() {
        return this.avsender;
    }

    @java.lang.SuppressWarnings("all")
    public String getJournalpostId() {
        return this.journalpostId;
    }

    @java.lang.SuppressWarnings("all")
    public String getDokumentId() {
        return this.dokumentId;
    }

    @java.lang.SuppressWarnings("all")
    public Long getGsakSaksnummer() {
        return this.gsakSaksnummer;
    }

    @java.lang.SuppressWarnings("all")
    public String getAktoerId() {
        return this.aktoerId;
    }

    @java.lang.SuppressWarnings("all")
    public List<Statsborgerskap> getStatsborgerskap() {
        return this.statsborgerskap;
    }

    @java.lang.SuppressWarnings("all")
    public List<Arbeidssted> getArbeidssteder() {
        return this.arbeidssteder;
    }

    @java.lang.SuppressWarnings("all")
    public List<Arbeidsland> getArbeidsland() {
        return this.arbeidsland;
    }

    @java.lang.SuppressWarnings("all")
    public Periode getPeriode() {
        return this.periode;
    }

    @java.lang.SuppressWarnings("all")
    public String getLovvalgsland() {
        return this.lovvalgsland;
    }

    @java.lang.SuppressWarnings("all")
    public String getArtikkel() {
        return this.artikkel;
    }

    @java.lang.SuppressWarnings("all")
    public boolean isErEndring() {
        return this.erEndring;
    }

    @java.lang.SuppressWarnings("all")
    public boolean isMidlertidigBestemmelse() {
        return this.midlertidigBestemmelse;
    }

    @java.lang.SuppressWarnings("all")
    public boolean isX006NavErFjernet() {
        return this.x006NavErFjernet;
    }

    @java.lang.SuppressWarnings("all")
    public String getYtterligereInformasjon() {
        return this.ytterligereInformasjon;
    }

    @java.lang.SuppressWarnings("all")
    public String getBucType() {
        return this.bucType;
    }

    @java.lang.SuppressWarnings("all")
    public String getSedType() {
        return this.sedType;
    }

    @java.lang.SuppressWarnings("all")
    public SvarAnmodningUnntak getSvarAnmodningUnntak() {
        return this.svarAnmodningUnntak;
    }

    @java.lang.SuppressWarnings("all")
    public AnmodningUnntak getAnmodningUnntak() {
        return this.anmodningUnntak;
    }

    @java.lang.SuppressWarnings("all")
    public void setSedId(final String sedId) {
        this.sedId = sedId;
    }

    @java.lang.SuppressWarnings("all")
    public void setSedVersjon(final String sedVersjon) {
        this.sedVersjon = sedVersjon;
    }

    @java.lang.SuppressWarnings("all")
    public void setRinaSaksnummer(final String rinaSaksnummer) {
        this.rinaSaksnummer = rinaSaksnummer;
    }

    @java.lang.SuppressWarnings("all")
    public void setAvsender(final Avsender avsender) {
        this.avsender = avsender;
    }

    @java.lang.SuppressWarnings("all")
    public void setJournalpostId(final String journalpostId) {
        this.journalpostId = journalpostId;
    }

    @java.lang.SuppressWarnings("all")
    public void setDokumentId(final String dokumentId) {
        this.dokumentId = dokumentId;
    }

    @java.lang.SuppressWarnings("all")
    public void setGsakSaksnummer(final Long gsakSaksnummer) {
        this.gsakSaksnummer = gsakSaksnummer;
    }

    @java.lang.SuppressWarnings("all")
    public void setAktoerId(final String aktoerId) {
        this.aktoerId = aktoerId;
    }

    @java.lang.SuppressWarnings("all")
    public void setStatsborgerskap(final List<Statsborgerskap> statsborgerskap) {
        this.statsborgerskap = statsborgerskap;
    }

    @java.lang.SuppressWarnings("all")
    public void setArbeidssteder(final List<Arbeidssted> arbeidssteder) {
        this.arbeidssteder = arbeidssteder;
    }

    @java.lang.SuppressWarnings("all")
    public void setArbeidsland(final List<Arbeidsland> arbeidsland) {
        this.arbeidsland = arbeidsland;
    }

    @java.lang.SuppressWarnings("all")
    public void setPeriode(final Periode periode) {
        this.periode = periode;
    }

    @java.lang.SuppressWarnings("all")
    public void setLovvalgsland(final String lovvalgsland) {
        this.lovvalgsland = lovvalgsland;
    }

    @java.lang.SuppressWarnings("all")
    public void setArtikkel(final String artikkel) {
        this.artikkel = artikkel;
    }

    @java.lang.SuppressWarnings("all")
    public void setErEndring(final boolean erEndring) {
        this.erEndring = erEndring;
    }

    @java.lang.SuppressWarnings("all")
    public void setMidlertidigBestemmelse(final boolean midlertidigBestemmelse) {
        this.midlertidigBestemmelse = midlertidigBestemmelse;
    }

    @java.lang.SuppressWarnings("all")
    public void setX006NavErFjernet(final boolean x006NavErFjernet) {
        this.x006NavErFjernet = x006NavErFjernet;
    }

    @java.lang.SuppressWarnings("all")
    public void setYtterligereInformasjon(final String ytterligereInformasjon) {
        this.ytterligereInformasjon = ytterligereInformasjon;
    }

    @java.lang.SuppressWarnings("all")
    public void setBucType(final String bucType) {
        this.bucType = bucType;
    }

    @java.lang.SuppressWarnings("all")
    public void setSedType(final String sedType) {
        this.sedType = sedType;
    }

    @java.lang.SuppressWarnings("all")
    public void setSvarAnmodningUnntak(final SvarAnmodningUnntak svarAnmodningUnntak) {
        this.svarAnmodningUnntak = svarAnmodningUnntak;
    }

    @java.lang.SuppressWarnings("all")
    public void setAnmodningUnntak(final AnmodningUnntak anmodningUnntak) {
        this.anmodningUnntak = anmodningUnntak;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof MelosysEessiMelding)) return false;
        final MelosysEessiMelding other = (MelosysEessiMelding) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        if (this.isErEndring() != other.isErEndring()) return false;
        if (this.isMidlertidigBestemmelse() != other.isMidlertidigBestemmelse()) return false;
        if (this.isX006NavErFjernet() != other.isX006NavErFjernet()) return false;
        final java.lang.Object this$gsakSaksnummer = this.getGsakSaksnummer();
        final java.lang.Object other$gsakSaksnummer = other.getGsakSaksnummer();
        if (this$gsakSaksnummer == null ? other$gsakSaksnummer != null : !this$gsakSaksnummer.equals(other$gsakSaksnummer))
            return false;
        final java.lang.Object this$sedId = this.getSedId();
        final java.lang.Object other$sedId = other.getSedId();
        if (this$sedId == null ? other$sedId != null : !this$sedId.equals(other$sedId)) return false;
        final java.lang.Object this$sedVersjon = this.getSedVersjon();
        final java.lang.Object other$sedVersjon = other.getSedVersjon();
        if (this$sedVersjon == null ? other$sedVersjon != null : !this$sedVersjon.equals(other$sedVersjon))
            return false;
        final java.lang.Object this$rinaSaksnummer = this.getRinaSaksnummer();
        final java.lang.Object other$rinaSaksnummer = other.getRinaSaksnummer();
        if (this$rinaSaksnummer == null ? other$rinaSaksnummer != null : !this$rinaSaksnummer.equals(other$rinaSaksnummer))
            return false;
        final java.lang.Object this$avsender = this.getAvsender();
        final java.lang.Object other$avsender = other.getAvsender();
        if (this$avsender == null ? other$avsender != null : !this$avsender.equals(other$avsender)) return false;
        final java.lang.Object this$journalpostId = this.getJournalpostId();
        final java.lang.Object other$journalpostId = other.getJournalpostId();
        if (this$journalpostId == null ? other$journalpostId != null : !this$journalpostId.equals(other$journalpostId))
            return false;
        final java.lang.Object this$dokumentId = this.getDokumentId();
        final java.lang.Object other$dokumentId = other.getDokumentId();
        if (this$dokumentId == null ? other$dokumentId != null : !this$dokumentId.equals(other$dokumentId))
            return false;
        final java.lang.Object this$aktoerId = this.getAktoerId();
        final java.lang.Object other$aktoerId = other.getAktoerId();
        if (this$aktoerId == null ? other$aktoerId != null : !this$aktoerId.equals(other$aktoerId)) return false;
        final java.lang.Object this$statsborgerskap = this.getStatsborgerskap();
        final java.lang.Object other$statsborgerskap = other.getStatsborgerskap();
        if (this$statsborgerskap == null ? other$statsborgerskap != null : !this$statsborgerskap.equals(other$statsborgerskap))
            return false;
        final java.lang.Object this$arbeidssteder = this.getArbeidssteder();
        final java.lang.Object other$arbeidssteder = other.getArbeidssteder();
        if (this$arbeidssteder == null ? other$arbeidssteder != null : !this$arbeidssteder.equals(other$arbeidssteder))
            return false;
        final java.lang.Object this$arbeidsland = this.getArbeidsland();
        final java.lang.Object other$arbeidsland = other.getArbeidsland();
        if (this$arbeidsland == null ? other$arbeidsland != null : !this$arbeidsland.equals(other$arbeidsland))
            return false;
        final java.lang.Object this$periode = this.getPeriode();
        final java.lang.Object other$periode = other.getPeriode();
        if (this$periode == null ? other$periode != null : !this$periode.equals(other$periode)) return false;
        final java.lang.Object this$lovvalgsland = this.getLovvalgsland();
        final java.lang.Object other$lovvalgsland = other.getLovvalgsland();
        if (this$lovvalgsland == null ? other$lovvalgsland != null : !this$lovvalgsland.equals(other$lovvalgsland))
            return false;
        final java.lang.Object this$artikkel = this.getArtikkel();
        final java.lang.Object other$artikkel = other.getArtikkel();
        if (this$artikkel == null ? other$artikkel != null : !this$artikkel.equals(other$artikkel)) return false;
        final java.lang.Object this$ytterligereInformasjon = this.getYtterligereInformasjon();
        final java.lang.Object other$ytterligereInformasjon = other.getYtterligereInformasjon();
        if (this$ytterligereInformasjon == null ? other$ytterligereInformasjon != null : !this$ytterligereInformasjon.equals(other$ytterligereInformasjon))
            return false;
        final java.lang.Object this$bucType = this.getBucType();
        final java.lang.Object other$bucType = other.getBucType();
        if (this$bucType == null ? other$bucType != null : !this$bucType.equals(other$bucType)) return false;
        final java.lang.Object this$sedType = this.getSedType();
        final java.lang.Object other$sedType = other.getSedType();
        if (this$sedType == null ? other$sedType != null : !this$sedType.equals(other$sedType)) return false;
        final java.lang.Object this$svarAnmodningUnntak = this.getSvarAnmodningUnntak();
        final java.lang.Object other$svarAnmodningUnntak = other.getSvarAnmodningUnntak();
        if (this$svarAnmodningUnntak == null ? other$svarAnmodningUnntak != null : !this$svarAnmodningUnntak.equals(other$svarAnmodningUnntak))
            return false;
        final java.lang.Object this$anmodningUnntak = this.getAnmodningUnntak();
        final java.lang.Object other$anmodningUnntak = other.getAnmodningUnntak();
        if (this$anmodningUnntak == null ? other$anmodningUnntak != null : !this$anmodningUnntak.equals(other$anmodningUnntak))
            return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof MelosysEessiMelding;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isErEndring() ? 79 : 97);
        result = result * PRIME + (this.isMidlertidigBestemmelse() ? 79 : 97);
        result = result * PRIME + (this.isX006NavErFjernet() ? 79 : 97);
        final java.lang.Object $gsakSaksnummer = this.getGsakSaksnummer();
        result = result * PRIME + ($gsakSaksnummer == null ? 43 : $gsakSaksnummer.hashCode());
        final java.lang.Object $sedId = this.getSedId();
        result = result * PRIME + ($sedId == null ? 43 : $sedId.hashCode());
        final java.lang.Object $sedVersjon = this.getSedVersjon();
        result = result * PRIME + ($sedVersjon == null ? 43 : $sedVersjon.hashCode());
        final java.lang.Object $rinaSaksnummer = this.getRinaSaksnummer();
        result = result * PRIME + ($rinaSaksnummer == null ? 43 : $rinaSaksnummer.hashCode());
        final java.lang.Object $avsender = this.getAvsender();
        result = result * PRIME + ($avsender == null ? 43 : $avsender.hashCode());
        final java.lang.Object $journalpostId = this.getJournalpostId();
        result = result * PRIME + ($journalpostId == null ? 43 : $journalpostId.hashCode());
        final java.lang.Object $dokumentId = this.getDokumentId();
        result = result * PRIME + ($dokumentId == null ? 43 : $dokumentId.hashCode());
        final java.lang.Object $aktoerId = this.getAktoerId();
        result = result * PRIME + ($aktoerId == null ? 43 : $aktoerId.hashCode());
        final java.lang.Object $statsborgerskap = this.getStatsborgerskap();
        result = result * PRIME + ($statsborgerskap == null ? 43 : $statsborgerskap.hashCode());
        final java.lang.Object $arbeidssteder = this.getArbeidssteder();
        result = result * PRIME + ($arbeidssteder == null ? 43 : $arbeidssteder.hashCode());
        final java.lang.Object $arbeidsland = this.getArbeidsland();
        result = result * PRIME + ($arbeidsland == null ? 43 : $arbeidsland.hashCode());
        final java.lang.Object $periode = this.getPeriode();
        result = result * PRIME + ($periode == null ? 43 : $periode.hashCode());
        final java.lang.Object $lovvalgsland = this.getLovvalgsland();
        result = result * PRIME + ($lovvalgsland == null ? 43 : $lovvalgsland.hashCode());
        final java.lang.Object $artikkel = this.getArtikkel();
        result = result * PRIME + ($artikkel == null ? 43 : $artikkel.hashCode());
        final java.lang.Object $ytterligereInformasjon = this.getYtterligereInformasjon();
        result = result * PRIME + ($ytterligereInformasjon == null ? 43 : $ytterligereInformasjon.hashCode());
        final java.lang.Object $bucType = this.getBucType();
        result = result * PRIME + ($bucType == null ? 43 : $bucType.hashCode());
        final java.lang.Object $sedType = this.getSedType();
        result = result * PRIME + ($sedType == null ? 43 : $sedType.hashCode());
        final java.lang.Object $svarAnmodningUnntak = this.getSvarAnmodningUnntak();
        result = result * PRIME + ($svarAnmodningUnntak == null ? 43 : $svarAnmodningUnntak.hashCode());
        final java.lang.Object $anmodningUnntak = this.getAnmodningUnntak();
        result = result * PRIME + ($anmodningUnntak == null ? 43 : $anmodningUnntak.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "MelosysEessiMelding(sedId=" + this.getSedId() + ", sedVersjon=" + this.getSedVersjon() + ", rinaSaksnummer=" + this.getRinaSaksnummer() + ", avsender=" + this.getAvsender() + ", journalpostId=" + this.getJournalpostId() + ", dokumentId=" + this.getDokumentId() + ", gsakSaksnummer=" + this.getGsakSaksnummer() + ", aktoerId=" + this.getAktoerId() + ", statsborgerskap=" + this.getStatsborgerskap() + ", arbeidssteder=" + this.getArbeidssteder() + ", arbeidsland=" + this.getArbeidsland() + ", periode=" + this.getPeriode() + ", lovvalgsland=" + this.getLovvalgsland() + ", artikkel=" + this.getArtikkel() + ", erEndring=" + this.isErEndring() + ", midlertidigBestemmelse=" + this.isMidlertidigBestemmelse() + ", x006NavErFjernet=" + this.isX006NavErFjernet() + ", ytterligereInformasjon=" + this.getYtterligereInformasjon() + ", bucType=" + this.getBucType() + ", sedType=" + this.getSedType() + ", svarAnmodningUnntak=" + this.getSvarAnmodningUnntak() + ", anmodningUnntak=" + this.getAnmodningUnntak() + ")";
    }
}
