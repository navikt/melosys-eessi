package no.nav.melosys.eessi.models;

import java.time.LocalDateTime;
import javax.persistence.*;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity(name = "sed_mottatt_hendelse")
@Data
@NoArgsConstructor
@EqualsAndHashCode
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class BucIdentifisert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "aktoer_id")
    private String aktoerId;

    @Column(name = "rina_saksnummer")
    private String rinaSaksnummer;

    @Column(name = "identifisert_av")
    private String identifisertAv;

    @Column(name = "identifisering_tidspunkt")
    private LocalDateTime identifiseringTidspunkt;

}
