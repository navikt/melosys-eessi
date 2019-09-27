package no.nav.melosys.eessi.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SedKontekst {
    //Person-aøk
    private boolean forsoktIdentifisert;
    private String navIdent;

    //Opprette journalpost
    private String journalpostID;
    private String dokumentID;
    private String gsakSaksnummer;

    //Oppgave til ID og fordeling
    private String oppgaveID;

    //Publisert til kafka
    private boolean publisertKafka;

    public boolean journalpostOpprettet() {
        return journalpostID != null && !journalpostID.isEmpty();
    }

    public boolean personErIdentifisert() {
        return navIdent != null && !navIdent.isEmpty();
    }

    public boolean identifiseringsOppgaveOpprettet() {
        return oppgaveID != null && !oppgaveID.isEmpty();
    }
}
