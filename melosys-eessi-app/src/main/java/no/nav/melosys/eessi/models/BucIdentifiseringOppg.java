package no.nav.melosys.eessi.models;

import javax.persistence.*;

import lombok.*;

@Entity(name = "buc_identifisering_oppg")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class BucIdentifiseringOppg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "rina_saksnummer")
    private String rinaSaksnummer;

    @Column(name = "oppgave_id")
    private String oppgaveId;

    @Column(name = "versjon")
    private int versjon;
}
