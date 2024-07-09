package no.nav.melosys.eessi.models

class SedKontekst {
    //Person-søk
    var isForsoktIdentifisert: Boolean = false
    var navIdent: String? = null

    //Opprette journalpost
    var journalpostID: String? = null
    var dokumentID: String? = null
    var gsakSaksnummer: String? = null

    //Oppgave til ID og fordeling
    var oppgaveID: String? = null

    //Publisert til kafka
    var isPublisertKafka: Boolean = false

    fun journalpostOpprettet(): Boolean {
        return journalpostID != null && !journalpostID!!.isEmpty()
    }

    fun personErIdentifisert(): Boolean {
        return navIdent != null && !navIdent!!.isEmpty()
    }

    fun identifiseringsOppgaveOpprettet(): Boolean {
        return oppgaveID != null && !oppgaveID!!.isEmpty()
    }

    override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (o !is SedKontekst) return false
        val other = o
        if (!other.canEqual(this as Any)) return false
        if (this.isForsoktIdentifisert != other.isForsoktIdentifisert) return false
        if (this.isPublisertKafka != other.isPublisertKafka) return false
        val `this$navIdent`: Any? = this.navIdent
        val `other$navIdent`: Any? = other.navIdent
        if (if (`this$navIdent` == null) `other$navIdent` != null else `this$navIdent` != `other$navIdent`) return false
        val `this$journalpostID`: Any? = this.journalpostID
        val `other$journalpostID`: Any? = other.journalpostID
        if (if (`this$journalpostID` == null) `other$journalpostID` != null else `this$journalpostID` != `other$journalpostID`) return false
        val `this$dokumentID`: Any? = this.dokumentID
        val `other$dokumentID`: Any? = other.dokumentID
        if (if (`this$dokumentID` == null) `other$dokumentID` != null else `this$dokumentID` != `other$dokumentID`) return false
        val `this$gsakSaksnummer`: Any? = this.gsakSaksnummer
        val `other$gsakSaksnummer`: Any? = other.gsakSaksnummer
        if (if (`this$gsakSaksnummer` == null) `other$gsakSaksnummer` != null else `this$gsakSaksnummer` != `other$gsakSaksnummer`) return false
        val `this$oppgaveID`: Any? = this.oppgaveID
        val `other$oppgaveID`: Any? = other.oppgaveID
        if (if (`this$oppgaveID` == null) `other$oppgaveID` != null else `this$oppgaveID` != `other$oppgaveID`) return false
        return true
    }

    protected fun canEqual(other: Any?): Boolean {
        return other is SedKontekst
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        result = result * PRIME + (if (this.isForsoktIdentifisert) 79 else 97)
        result = result * PRIME + (if (this.isPublisertKafka) 79 else 97)
        val `$navIdent`: Any? = this.navIdent
        result = result * PRIME + (`$navIdent`?.hashCode() ?: 43)
        val `$journalpostID`: Any? = this.journalpostID
        result = result * PRIME + (`$journalpostID`?.hashCode() ?: 43)
        val `$dokumentID`: Any? = this.dokumentID
        result = result * PRIME + (`$dokumentID`?.hashCode() ?: 43)
        val `$gsakSaksnummer`: Any? = this.gsakSaksnummer
        result = result * PRIME + (`$gsakSaksnummer`?.hashCode() ?: 43)
        val `$oppgaveID`: Any? = this.oppgaveID
        result = result * PRIME + (`$oppgaveID`?.hashCode() ?: 43)
        return result
    }

    override fun toString(): String {
        return "SedKontekst(forsoktIdentifisert=" + this.isForsoktIdentifisert + ", navIdent=" + this.navIdent + ", journalpostID=" + this.journalpostID + ", dokumentID=" + this.dokumentID + ", gsakSaksnummer=" + this.gsakSaksnummer + ", oppgaveID=" + this.oppgaveID + ", publisertKafka=" + this.isPublisertKafka + ")"
    }
}
