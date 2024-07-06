package no.nav.melosys.eessi.models.buc

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.melosys.eessi.controller.dto.SedStatus
import no.nav.melosys.eessi.models.BucType
import no.nav.melosys.eessi.models.SedType
import java.time.ZonedDateTime
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class BUC @JsonCreator constructor(
    @JsonProperty("id") var id: String? = null,
    @JsonProperty("startDate") var startDate: ZonedDateTime? = null,
    @JsonProperty("lastUpdate") var lastUpdate: ZonedDateTime? = null,
    @JsonProperty("status") var status: String? = null,
    @JsonProperty("creator") var creator: Creator? = null, // TODO: gjør denne none-nullable
    @JsonProperty("documents") var documents: List<Document> = ArrayList(),
    @JsonProperty("actions") var actions: List<Action> = ArrayList(),
    @JsonProperty("processDefinitionName") var bucType: String? = null, // TODO: gjør denne none-nullable
    @JsonProperty("processDefinitionVersion") var bucVersjon: String? = null,
    @JsonProperty("participants") var participants: Collection<Participant> = ArrayList(),
    @JsonProperty("internationalId") var internationalId: String? = null
) {
    fun hentAvsenderLand(): String = creator!!.organisation!!.countryCode!!

    fun kanOppretteEllerOppdatereSed(sedType: SedType): Boolean = actions.any {
        it.documentType.equals(sedType.name, ignoreCase = true) &&
            it.operation?.uppercase() in setOf("UPDATE", "CREATE")
    }

    fun hentDokument(dokumentID: String): Document =
        documents.firstOrNull { it.id.equals(dokumentID, ignoreCase = true) }
            ?: throw NoSuchElementException("No document found with ID: $dokumentID")

    fun hentSistOppdaterteDocument(): Optional<Document> {
        return documents.filter { SedStatus.erGyldigEngelskStatus(it.status) }
            .maxWithOrNull(compareBy { it.lastUpdate })
            .let { Optional.ofNullable(it) }
    }

    fun erÅpen(): Boolean = !"closed".equals(status, ignoreCase = true)

    fun finnDokumentVedSedType(sedType: String): Optional<Document> =
        finnDokumenterVedSedType(sedType).minWithOrNull(Comparator.comparing { document: Document -> SedStatus.fraEngelskStatus(document.status) })
            .let { Optional.ofNullable(it) }

    fun sedKanOppdateres(id: String): Boolean = actions.filter { id == it.documentId }
        .any { "Update".equals(it.operation, ignoreCase = true) }

    fun kanLukkesAutomatisk(): Boolean = when (BucType.valueOf(bucType!!)) {
        BucType.LA_BUC_06 ->
            harMottattSedTypeAntallDagerSiden(SedType.A006, 30) && kanOppretteEllerOppdatereSed(SedType.X001)

        BucType.LA_BUC_01 -> {
            val harMottattA002EllerA011 = harMottattSedTypeAntallDagerSiden(SedType.A002, 60) ||
                harMottattSedTypeAntallDagerSiden(SedType.A011, 60)
            val sisteMottattLovvalgSED = finnSistMottattSED { it.erLovvalgSED() }
                .map { it.erAntallDagerSidenOppdatering(60) }
                .orElse(false)

            harMottattA002EllerA011 && kanOppretteEllerOppdatereSed(SedType.X001) && sisteMottattLovvalgSED
        }

        BucType.LA_BUC_03 -> {
            val harMottattX012EllerSendtX013EllerA008 =
                (finnDokumentVedTypeOgStatus(SedType.X012, SedStatus.MOTTATT).isEmpty || harMottattSedTypeAntallDagerSiden(SedType.X012, 30)) &&
                    (finnDokumentVedTypeOgStatus(SedType.X013, SedStatus.SENDT).isEmpty || harSendtSedTypeAntallDagerSiden(SedType.X013, 30)) &&
                    harSendtSedTypeAntallDagerSiden(SedType.A008, 30)

            harMottattX012EllerSendtX013EllerA008 && kanOppretteEllerOppdatereSed(SedType.X001)
        }

        else -> kanOppretteEllerOppdatereSed(SedType.X001)
    }

    fun finnFørstMottatteSed(): Optional<Document> =
        documents.stream().filter { obj: Document -> obj.erInngående() }.filter { obj: Document -> obj.erOpprettet() }
            .filter { obj: Document -> obj.erIkkeX100() }
            .min(Comparator.comparing { obj: Document -> obj.creationDate })

    fun hentMottakere(): Set<String> = participants
        .filter { it.erMotpart() }
        .map { it.organisation.id!! }
        .toSet()

    private fun finnDokumenterVedSedType(sedType: String): List<Document> = documents.filter { d: Document -> sedType == d.type }

    private fun finnDokumentVedTypeOgStatus(sedType: SedType, status: SedStatus): Optional<Document> =
        finnDokumenterVedSedType(sedType.name)
            .firstOrNull { d: Document -> status.engelskStatus == d.status }
            .let { Optional.ofNullable(it) }

    private fun harMottattSedTypeAntallDagerSiden(sedType: SedType, minstAntallDagerSidenMottatt: Long): Boolean =
        finnDokumentVedTypeOgStatus(sedType, SedStatus.MOTTATT).filter {
            it.erAntallDagerSidenOppdatering(minstAntallDagerSidenMottatt)
        }.isPresent

    private fun harSendtSedTypeAntallDagerSiden(sedType: SedType, minstAntallDagerSidenMottatt: Long): Boolean =
        finnDokumentVedTypeOgStatus(sedType, SedStatus.SENDT).filter {
            it.erAntallDagerSidenOppdatering(minstAntallDagerSidenMottatt)
        }.isPresent

    private fun finnSistMottattSED(documentPredicate: (Document) -> Boolean): Optional<Document> = documents
        .filter { it.erInngående() }
        .filter { it.erOpprettet() }
        .filter(documentPredicate)
        .maxByOrNull { it.lastUpdate!! }
        .let { Optional.ofNullable(it) }
}
