package no.nav.melosys.eessi.models.kafkadlq

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@DiscriminatorValue("SED_MOTTATT_HENDELSE")
class SedMottattHendelseKafkaDLQ : KafkaDLQ() {
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "melding", columnDefinition = "jsonb")
    var sedMottattHendelse: SedHendelse? = null

    override fun hentMeldingSomStreng(): String = ObjectMapper().writeValueAsString(sedMottattHendelse)
}
