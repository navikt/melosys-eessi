package no.nav.melosys.eessi.service.mottak

import no.nav.melosys.eessi.models.SedMottattHendelse
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.repository.SedMottattLager
import no.nav.melosys.eessi.repository.SedMottattLagerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class SedLagerService(
    private val sedMottattLagerRepository: SedMottattLagerRepository
) {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun lagreSedSeparatTransaksjon(sedMottatt: SedMottattHendelse, sed: SED, toggleAktivert: Boolean = false) {
        sedMottattLagerRepository.save(
            SedMottattLager(
                sedId = sedMottatt.sedHendelse.sedId,
                sed = sed,
                storageReason = "TREDJELANDSBORGER 7403 - toggle:$toggleAktivert",
            )
        )
    }
}
