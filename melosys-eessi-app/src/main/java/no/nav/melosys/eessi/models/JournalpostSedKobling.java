package no.nav.melosys.eessi.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
}
