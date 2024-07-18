package no.nav.melosys.eessi.service.journalfoering

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain
import io.mockk.junit5.MockKExtension
import java.lang.IllegalArgumentException

@ExtendWith(MockKExtension::class)
internal class JournalpostMetadataServiceTest {
    private val journalpostMetadataService = JournalpostMetadataService()

    @Test
    fun hentJournalpostMetadata_ugyldigSedType_kasterFeil() {
        shouldThrow<IllegalArgumentException> {
            journalpostMetadataService.hentJournalpostMetadata("ugyldig SedType")
        }.message shouldContain "No enum constant"
    }
}
