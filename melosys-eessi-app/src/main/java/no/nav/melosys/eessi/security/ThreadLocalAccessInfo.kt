package no.nav.melosys.eessi.security

import mu.KotlinLogging

private val log = KotlinLogging.logger {}

/**
 * Thread-local kontekst for å håndtere tilgangsinformasjon og bestemme tokentype
 * basert på type forespørsel (web, admin, prosess).
 */
class ThreadLocalAccessInfo {
    private var erAdminForespørsel: Boolean = false
    private var brukerId: String? = null

    private fun erFraAdminForespørsel(): Boolean = erAdminForespørsel
    private fun hentBrukerId(): String? = brukerId

    companion object {
        private val threadLocalLagring = ThreadLocal.withInitial { ThreadLocalAccessInfo() }

        fun hentBrukerId(): String? {
            val info = threadLocalLagring.get()
            return info.hentBrukerId()
        }

        fun skalBrukeSystemToken(): Boolean {
            val info = threadLocalLagring.get()
            return info.erFraAdminForespørsel()
        }

        fun <T> utførSomAdminForespørsel(handling: () -> T): T {
            val info = threadLocalLagring.get()
            val tidligereErAdminForespørselVerdi = info.erAdminForespørsel

            info.erAdminForespørsel = true
            log.info("Utfører handling som admin forespørsel, tidligere tidligereErAdminForespørselVerdi=$tidligereErAdminForespørselVerdi")
            return try {
                handling()
            } finally {
                info.erAdminForespørsel = tidligereErAdminForespørselVerdi
                if (!tidligereErAdminForespørselVerdi) {
                    threadLocalLagring.remove()
                }
            }
        }

        fun hentInfo(): String {
            return threadLocalLagring.get().toString()
        }
    }

    override fun toString(): String {
        return "ThreadLocalAccessInfo(" +
                "erAdminForespørsel=$erAdminForespørsel, " +
                "brukerId='$brukerId'" +
                ")"
    }
}
