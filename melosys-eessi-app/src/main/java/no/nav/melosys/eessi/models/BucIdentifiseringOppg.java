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

@Entity(name = "buc_identifisering_oppg")
@Data
@NoArgsConstructor
@EqualsAndHashCode
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class BucIdentifiseringOppg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "rina_saksnummer")
    private String rinaSaksnummer;

    @Column(name = "oppgave_id")
    private String oppgaveId;
}
