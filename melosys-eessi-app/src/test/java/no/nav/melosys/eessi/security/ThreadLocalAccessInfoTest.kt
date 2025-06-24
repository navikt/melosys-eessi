package no.nav.melosys.eessi.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ThreadLocalAccessInfoTest {

    @Test
    fun `skalBrukeSystemToken - standard tilstand skal returnere false`() {
        // Standard tilstand skal ikke være admin-forespørsel
        assertThat(ThreadLocalAccessInfo.skalBrukeSystemToken()).isFalse()
    }

    @Test
    fun `skalBrukeSystemToken - under admin-forespørsel utførelse skal returnere true`() {
        var systemTokenBrukt = false
        
        ThreadLocalAccessInfo.utførSomAdminForespørsel {
            systemTokenBrukt = ThreadLocalAccessInfo.skalBrukeSystemToken()
        }
        
        assertThat(systemTokenBrukt).isTrue()
        // Etter utførelse skal det være tilbake til normalt
        assertThat(ThreadLocalAccessInfo.skalBrukeSystemToken()).isFalse()
    }

    @Test
    fun `utførSomAdminForespørsel - skal utføre lambda og returnere resultat`() {
        val resultat = ThreadLocalAccessInfo.utførSomAdminForespørsel {
            "test-resultat"
        }
        
        assertThat(resultat).isEqualTo("test-resultat")
    }

    @Test
    fun `utførSomAdminForespørsel - skal håndtere exceptions riktig`() {
        val exception = assertThrows<RuntimeException> {
            ThreadLocalAccessInfo.utførSomAdminForespørsel {
                throw RuntimeException("Test exception")
            }
        }
        
        assertThat(exception.message).isEqualTo("Test exception")
        // Skal være ryddet opp etter exception
        assertThat(ThreadLocalAccessInfo.skalBrukeSystemToken()).isFalse()
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
        
        assertThat(resultater).containsExactly(true, true, true)
        // Etter alle nestede kall skal det være tilbake til normalt
        assertThat(ThreadLocalAccessInfo.skalBrukeSystemToken()).isFalse()
    }

    @Test
    fun `hentBrukerId - skal returnere null som standard`() {
        assertThat(ThreadLocalAccessInfo.hentBrukerId()).isNull()
    }

    @Test
    fun `hentInfo - skal returnere string-representasjon`() {
        val info = ThreadLocalAccessInfo.hentInfo()
        assertThat(info).contains("ThreadLocalAccessInfo")
        assertThat(info).contains("erAdminForespørsel=false")
    }

    @Test
    fun `hentInfo - under admin-forespørsel skal vise admin-tilstand`() {
        var infoStreng = ""
        
        ThreadLocalAccessInfo.utførSomAdminForespørsel {
            infoStreng = ThreadLocalAccessInfo.hentInfo()
        }
        
        assertThat(infoStreng).contains("erAdminForespørsel=true")
    }
} 