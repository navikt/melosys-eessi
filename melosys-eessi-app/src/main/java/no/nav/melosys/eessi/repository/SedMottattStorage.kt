package no.nav.melosys.eessi.repository

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import no.nav.melosys.eessi.models.sed.SED
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.ZonedDateTime

@Entity
@Table(name = "sed_storage")
class SedMottattStorage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "sed_id", nullable = false, length = 50)
    val sedId: String,

    @Column(name = "rina_saksnummer", nullable = false, length = 20)
    val rinaSaksnummer: String,

    @Column(name = "sed", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    val sed: SED,

    @Column(name = "storage_reason", nullable = false, length = 50)
    val storageReason: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: ZonedDateTime = ZonedDateTime.now()
)
