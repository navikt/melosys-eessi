package no.nav.melosys.eessi.models;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.common.aliasing.qual.Unique;

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
    @Unique
    private String rinaSaksnummer;

    @Column(name = "folkeregisterident", updatable = false)
    private String folkeregisterident;
}
