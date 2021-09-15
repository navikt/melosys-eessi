package no.nav.melosys.eessi.models;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "buc_identifisert")
public class BucIdentifisert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "rina_saksnummer", updatable = false)
    private String rinaSaksnummer;

    @Column(name = "folkeregisterident", updatable = false)
    private String folkeregisterident;
}
