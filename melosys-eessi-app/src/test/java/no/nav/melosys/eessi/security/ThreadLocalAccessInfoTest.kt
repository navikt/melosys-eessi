package no.nav.melosys.eessi.security

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test

class ThreadLocalAccessInfoTest {

    @Test
    fun `skalBrukeSystemToken - standard tilstand skal returnere false`() {
        // Standard tilstand skal ikke være admin-forespørsel
        ThreadLocalAccessInfo.skalBrukeSystemToken() shouldBe false
    }

    @Test
    fun `skalBrukeSystemToken - under admin-forespørsel utførelse skal returnere true`() {
        var systemTokenBrukt = false
        
        ThreadLocalAccessInfo.utførSomAdminForespørsel {
            systemTokenBrukt = ThreadLocalAccessInfo.skalBrukeSystemToken()
        }
        
        systemTokenBrukt shouldBe true
        // Etter utførelse skal det være tilbake til normalt
        ThreadLocalAccessInfo.skalBrukeSystemToken() shouldBe false
    }

    @Test
    fun `utførSomAdminForespørsel - skal utføre lambda og returnere resultat`() {
        val resultat = ThreadLocalAccessInfo.utførSomAdminForespørsel {
            "test-resultat"
        }
        
        resultat shouldBe "test-resultat"
    }

    @Test
    fun `utførSomAdminForespørsel - skal håndtere exceptions riktig`() {
        val exception = shouldThrow<RuntimeException> {
            ThreadLocalAccessInfo.utførSomAdminForespørsel {
                throw RuntimeException("Test exception")
            }
        }
        
        exception.message shouldBe "Test exception"
        // Skal være ryddet opp etter exception
        ThreadLocalAccessInfo.skalBrukeSystemToken() shouldBe false
    }

    @Test
    fun `utførSomAdminForespørsel - nestede kall skal fungere korrekt`() {
        val resultater = mutableListOf<Boolean>()
        
        ThreadLocalAccessInfo.utførSomAdminForespørsel {
            resultater.add(ThreadLocalAccessInfo.skalBrukeSystemToken()) // Skal være true
            
            ThreadLocalAccessInfo.utførSomAdminForespørsel {
                resultater.add(ThreadLocalAccessInfo.skalBrukeSystemToken()) // Skal fortsatt være true
            }
            
            resultater.add(ThreadLocalAccessInfo.skalBrukeSystemToken()) // Skal fortsatt være true
        }
        
        resultater shouldContainExactly listOf(true, true, true)
        // Etter alle nestede kall skal det være tilbake til normalt
        ThreadLocalAccessInfo.skalBrukeSystemToken() shouldBe false
    }

    @Test
    fun `hentBrukerId - skal returnere null som standard`() {
        ThreadLocalAccessInfo.hentBrukerId() shouldBe null
    }

    @Test
    fun `hentInfo - skal returnere string-representasjon`() {
        val info = ThreadLocalAccessInfo.hentInfo()
        info shouldContain "ThreadLocalAccessInfo"
        info shouldContain "erAdminForespørsel=false"
    }

    @Test
    fun `hentInfo - under admin-forespørsel skal vise admin-tilstand`() {
        var infoStreng = ""
        
        ThreadLocalAccessInfo.utførSomAdminForespørsel {
            infoStreng = ThreadLocalAccessInfo.hentInfo()
        }
        
        infoStreng shouldContain "erAdminForespørsel=true"
    }
} 