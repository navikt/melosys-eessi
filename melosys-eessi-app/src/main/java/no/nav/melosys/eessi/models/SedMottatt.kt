package no.nav.melosys.eessi.models

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType
import jakarta.persistence.*
import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity(name = "sed_mottatt")
@Convert(attributeName = "jsonb", converter = JsonBinaryType::class) // Tester kjører uten TODO: skriv test som viser om den trengs
class SedMottatt(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = 0,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sed_hendelse", nullable = false)
    var sedHendelse: SedHendelse,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sed_kontekst", nullable = false)
    var sedKontekst: SedKontekst,

    @Column(name = "versjon", nullable = false)
    var versjon: Int = 1,

    @CreatedDate
    @Column(name = "mottatt_dato")
    var mottattDato: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name = "endret_dato")
    var sistEndretDato: LocalDateTime? = null,

    @Column(name = "feilede_forsok", nullable = false)
    var feiledeForsok: Int = 0,

    @Column(name = "feilet", nullable = false)
    var feilet: Boolean = false,

    @Column(name = "ferdig", nullable = false)
    var ferdig: Boolean = false
) {
    companion object {
        fun av(sedHendelse: SedHendelse): SedMottatt {
            return SedMottatt(
                sedHendelse = sedHendelse,
                versjon = 1,
                sedKontekst = SedKontekst(),
                mottattDato = LocalDateTime.now(),
                sistEndretDato = LocalDateTime.now()
            )
        }
    }
}
