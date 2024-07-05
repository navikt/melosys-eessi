package no.nav.melosys.eessi.models.buc

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.melosys.eessi.controller.dto.SedStatus
import no.nav.melosys.eessi.models.BucType
import no.nav.melosys.eessi.models.SedType
import java.time.ZonedDateTime
import java.util.*
import java.util.function.Predicate
import java.util.stream.Collectors
import java.util.stream.Stream

@JsonIgnoreProperties(ignoreUnknown = true)
data class BUC @JsonCreator constructor(
    @JsonProperty("id") var id: String? = null,
    @JsonProperty("startDate") var startDate: ZonedDateTime? = null,
    @JsonProperty("lastUpdate") var lastUpdate: ZonedDateTime? = null,
    @JsonProperty("status") var status: String? = null,
    @JsonProperty("creator") var creator: Creator? = null,
    @JsonProperty("documents") var documents: List<Document> = ArrayList(),
    @JsonProperty("actions") var actions: List<Action> = ArrayList(),
    @JsonProperty("processDefinitionName") var bucType: String? = null,
    @JsonProperty("processDefinitionVersion") var bucVersjon: String? = null,
    @JsonProperty("participants") var participants: Collection<Participant> = ArrayList(),
    @JsonProperty("internationalId") var internationalId: String? = null
) {
    fun hentAvsenderLand(): String {
        return creator!!.organisation.countryCode
    }

    fun kanOppretteEllerOppdatereSed(sedType: SedType): Boolean {
        return actions.stream().filter { a: Action -> sedType.name.equals(a.documentType, ignoreCase = true) }
            .anyMatch { action: Action ->
                "CREATE".equals(action.operation, ignoreCase = true) || "UPDATE".equals(
                    action.operation,
                    ignoreCase = true
                )
            }
    }

    fun hentDokument(dokumentID: String?): Document {
        return documents.stream().filter { d: Document -> d.id.equals(dokumentID, ignoreCase = true) }.findAny().orElseThrow()
    }

    fun hentSistOppdaterteDocument(): Optional<Document> {
        return documents.stream().filter { d: Document -> SedStatus.erGyldigEngelskStatus(d.status) }.max(sistOppdatert)
    }

    fun erÅpen(): Boolean {
        return !"closed".equals(status, ignoreCase = true)
    }

    fun finnDokumentVedSedType(sedType: String): Optional<Document> {
        return finnDokumenterVedSedType(sedType).min(sorterEtterStatus)
    }

    private fun finnDokumenterVedSedType(sedType: String): Stream<Document> {
        return documents.stream().filter { d: Document -> sedType == d.type }
    }

    fun finnDokumentVedTypeOgStatus(sedType: SedType, status: SedStatus): Optional<Document> {
        return finnDokumenterVedSedType(sedType.name).filter { d: Document -> status.engelskStatus == d.status }.findFirst()
    }

    fun sedKanOppdateres(id: String): Boolean {
        return actions.stream().filter { action: Action -> id == action.documentId }
            .anyMatch { action: Action -> "Update".equals(action.operation, ignoreCase = true) }
    }

    fun harMottattSedTypeAntallDagerSiden(sedType: SedType, minstAntallDagerSidenMottatt: Long): Boolean {
        return finnDokumentVedTypeOgStatus(sedType, SedStatus.MOTTATT).filter { d: Document ->
            d.erAntallDagerSidenOppdatering(
                minstAntallDagerSidenMottatt
            )
        }.isPresent
    }

    fun harSendtSedTypeAntallDagerSiden(sedType: SedType, minstAntallDagerSidenMottatt: Long): Boolean {
        return finnDokumentVedTypeOgStatus(sedType, SedStatus.SENDT).filter { d: Document ->
            d.erAntallDagerSidenOppdatering(
                minstAntallDagerSidenMottatt
            )
        }.isPresent
    }

    fun kanLukkesAutomatisk(): Boolean {
        val bucTypeEnum = BucType.valueOf(bucType!!)
        if (bucTypeEnum == BucType.LA_BUC_06) {
            return harMottattSedTypeAntallDagerSiden(SedType.A006, 30) && kanOppretteEllerOppdatereSed(SedType.X001)
        } else if (bucTypeEnum == BucType.LA_BUC_01) {
            val harMottattA002EllerA011 = harMottattSedTypeAntallDagerSiden(SedType.A002, 60) || harMottattSedTypeAntallDagerSiden(SedType.A011, 60)
            return harMottattA002EllerA011 && kanOppretteEllerOppdatereSed(SedType.X001) && finnSistMottattSED { obj: Document -> obj.erLovvalgSED() }.map { d: Document ->
                d.erAntallDagerSidenOppdatering(
                    60
                )
            }
                .orElse(false)
        } else if (bucTypeEnum == BucType.LA_BUC_03) {
            val harMottattX012EllerSendtX013EllerA008 =
                (finnDokumentVedTypeOgStatus(SedType.X012, SedStatus.MOTTATT).isEmpty || harMottattSedTypeAntallDagerSiden(
                    SedType.X012,
                    30
                )) && (finnDokumentVedTypeOgStatus(SedType.X013, SedStatus.SENDT).isEmpty || harSendtSedTypeAntallDagerSiden(
                    SedType.X013,
                    30
                )) && harSendtSedTypeAntallDagerSiden(SedType.A008, 30)
            return harMottattX012EllerSendtX013EllerA008 && kanOppretteEllerOppdatereSed(SedType.X001)
        }
        return kanOppretteEllerOppdatereSed(SedType.X001)
    }

    private fun finnSistMottattSED(documentPredicate: Predicate<Document>): Optional<Document> {
        return documents.stream().filter { obj: Document -> obj.erInngående() }.filter { obj: Document -> obj.erOpprettet() }
            .filter(documentPredicate).max(Comparator.comparing { obj: Document -> obj.lastUpdate })
    }

    fun finnFørstMottatteSed(): Optional<Document> {
        return documents.stream().filter { obj: Document -> obj.erInngående() }.filter { obj: Document -> obj.erOpprettet() }
            .filter { obj: Document -> obj.erIkkeX100() }
            .min(Comparator.comparing { obj: Document -> obj.creationDate })
    }

    fun hentMottakere(): Set<String> {
        return participants.stream().filter { obj: Participant -> obj.erMotpart() }
            .map { p: Participant -> p.organisation.id }.collect(Collectors.toSet())
    }

    companion object {
        private val sistOppdatert: Comparator<Document> = Comparator.comparing { obj: Document -> obj.lastUpdate }
        private val sorterEtterStatus: Comparator<Document> =
            Comparator.comparing { document: Document -> SedStatus.fraEngelskStatus(document.status) }
    }
}
