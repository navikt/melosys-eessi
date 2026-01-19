package no.nav.melosys.eessi.models.kafkadlq

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import tools.jackson.databind.json.JsonMapper

@Entity
@DiscriminatorValue("SED_MOTTATT_HENDELSE")
class SedMottattHendelseKafkaDLQ : KafkaDLQ() {
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "melding", columnDefinition = "jsonb")
    var sedMottattHendelse: SedHendelse? = null

    override fun hentMeldingSomStreng(): String = JsonMapper.builder().build().writeValueAsString(sedMottattHendelse)
}
