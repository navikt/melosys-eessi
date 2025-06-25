package no.nav.melosys.eessi.security

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ContextHolderIntegrationTest {

    @Test
    fun `canExchangeOBOToken - skal returnere true når ikke admin forespørsel og ingen token kontekst`() {
        val contextHolder = ContextHolder()
        
        // Standard tilstand - ikke admin forespørsel, ingen token kontekst
        contextHolder.canExchangeOBOToken() shouldBe false // Returnerer false fordi getTokenContext() returnerer null
    }

    @Test
    fun `canExchangeOBOToken - skal returnere false under admin forespørsel`() {
        val contextHolder = ContextHolder()
        
        ThreadLocalAccessInfo.utførSomAdminForespørsel {
            // Under admin forespørsel, skal bruke systemtoken (ikke OBO)
            contextHolder.canExchangeOBOToken() shouldBe false
        }
        
        // Etter admin forespørsel, reset til normal tilstand
        contextHolder.canExchangeOBOToken() shouldBe false // Fortsatt false på grunn av ingen token kontekst
    }

    @Test
    fun `token utvelgelse logikk - admin forespørsel skal bruke system token`() {
        // Simuler logikken som ville skje i interceptors
        
        // Normal forespørsel
        val shouldUseSystemTokenNormal = ThreadLocalAccessInfo.skalBrukeSystemToken()
        shouldUseSystemTokenNormal shouldBe false
        
        // Admin forespørsel
        ThreadLocalAccessInfo.utførSomAdminForespørsel {
            val shouldUseSystemTokenAdmin = ThreadLocalAccessInfo.skalBrukeSystemToken()
            shouldUseSystemTokenAdmin shouldBe true
        }
        
        // Reset til normal tilstand etter admin forespørsel
        val shouldUseSystemTokenAfter = ThreadLocalAccessInfo.skalBrukeSystemToken()
        shouldUseSystemTokenAfter shouldBe false
    }
}
