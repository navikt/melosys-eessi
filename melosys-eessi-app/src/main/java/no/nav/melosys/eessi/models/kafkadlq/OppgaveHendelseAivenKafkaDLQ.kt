package no.nav.melosys.eessi.models.kafkadlq

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import no.nav.melosys.eessi.identifisering.OppgaveKafkaAivenRecord
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import tools.jackson.databind.json.JsonMapper

@Entity
@DiscriminatorValue("OPPGAVE_HENDELSE_AIVEN")
class OppgaveHendelseAivenKafkaDLQ : KafkaDLQ() {
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "melding", columnDefinition = "jsonb")
    var oppgaveEndretHendelse: OppgaveKafkaAivenRecord? = null

    override fun hentMeldingSomStreng(): String =
        JsonMapper.builder().build().writeValueAsString(oppgaveEndretHendelse)
}
