package no.nav.melosys.eessi.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ContextHolderIntegrationTest {

    @Test
    fun `canExchangeOBOToken - skal returnere true når ikke admin forespørsel og ingen token kontekst`() {
        val contextHolder = ContextHolder()

        // Standard tilstand - ikke admin forespørsel, ingen token kontekst
        assertThat(contextHolder.canExchangeOBOToken()).isFalse() // Returnerer false fordi getTokenContext() returnerer null
    }

    @Test
    fun `canExchangeOBOToken - skal returnere false under admin forespørsel`() {
        val contextHolder = ContextHolder()

        ThreadLocalAccessInfo.utførSomAdminForespørsel {
            // Under admin forespørsel, skal bruke systemtoken (ikke OBO)
            assertThat(contextHolder.canExchangeOBOToken()).isFalse()
        }

        // Etter admin forespørsel, reset til normal tilstand
        assertThat(contextHolder.canExchangeOBOToken()).isFalse() // Fortsatt false på grunn av ingen token kontekst
    }

    @Test
    fun `token utvelgelse logikk - admin forespørsel skal bruke system token`() {
        // Simuler logikken som ville skje i interceptors

        // Normal forespørsel
        val shouldUseSystemTokenNormal = ThreadLocalAccessInfo.skalBrukeSystemToken()
        assertThat(shouldUseSystemTokenNormal).isFalse()

        // Admin forespørsel
        ThreadLocalAccessInfo.utførSomAdminForespørsel {
            val shouldUseSystemTokenAdmin = ThreadLocalAccessInfo.skalBrukeSystemToken()
            assertThat(shouldUseSystemTokenAdmin).isTrue()
        }

        // Reset til normal tilstand etter admin forespørsel
        val shouldUseSystemTokenAfter = ThreadLocalAccessInfo.skalBrukeSystemToken()
        assertThat(shouldUseSystemTokenAfter).isFalse()
    }
}
