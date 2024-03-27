package no.nav.melosys.eessi.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "JOURNALPOST_SED_KOBLING")
public class JournalpostSedKobling {

    @Id
    @Column(name = "journalpost_id")
    private String journalpostID;

    @Column(name = "rina_saksnummer")
    private String rinaSaksnummer;

    @Column(name = "sed_id")
    private String sedId;

    @Column(name = "sed_versjon")
    private String sedVersjon;

    @Column(name = "buc_type")
    private String bucType;

    @Column(name = "sed_type")
    private String sedType;

    public boolean erASed() {
        return sedType.toUpperCase().startsWith("A");
    }
}
