package no.nav.melosys.eessi.models;

import java.time.LocalDateTime;
import jakarta.persistence.*;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity(name = "sed_mottatt")
@Data
@NoArgsConstructor
@EqualsAndHashCode
@Convert(attributeName = "jsonb", converter = JsonBinaryType.class)
public class SedMottatt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sed_hendelse")
    private SedHendelse sedHendelse;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sed_kontekst")
    private SedKontekst sedKontekst;

    @Column(name = "versjon")
    private int versjon = 1;

    @CreatedDate
    @Column(name = "mottatt_dato")
    private LocalDateTime mottattDato;

    @LastModifiedDate
    @Column(name = "endret_dato")
    private LocalDateTime sistEndretDato;

    @Column(name = "feilede_forsok")
    private int feiledeForsok;

    @Column(name = "feilet")
    private boolean feilet;

    @Column(name = "ferdig")
    private boolean ferdig;

    public static SedMottatt av(SedHendelse sedHendelse) {
        SedMottatt sedMottatt = new SedMottatt();
        sedMottatt.setSedHendelse(sedHendelse);
        sedMottatt.setVersjon(1);
        sedMottatt.setSedKontekst(new SedKontekst());
        sedMottatt.setMottattDato(LocalDateTime.now());
        sedMottatt.setSistEndretDato(LocalDateTime.now());

        return sedMottatt;
    }
}
