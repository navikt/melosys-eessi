package no.nav.melosys.eessi.models.kafkadlq

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import no.nav.melosys.eessi.identifisering.OppgaveKafkaAivenRecord
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@DiscriminatorValue("OPPGAVE_HENDELSE_AIVEN")
class OppgaveHendelseAivenKafkaDLQ : KafkaDLQ() {
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "melding", columnDefinition = "jsonb")
    var oppgaveEndretHendelse: OppgaveKafkaAivenRecord? = null

    @Throws(JsonProcessingException::class)
    override fun hentMeldingSomStreng(): String =
        ObjectMapper().registerModule(JavaTimeModule()).writeValueAsString(oppgaveEndretHendelse)
}
