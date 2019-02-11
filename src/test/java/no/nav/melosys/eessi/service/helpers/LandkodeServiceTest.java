package no.nav.melosys.eessi.service.helpers;

import no.nav.melosys.eessi.models.exception.LandkodeIkkeFunnetException;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeService;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4ClassRunner.class)
public class LandkodeServiceTest {

    private LandkodeService landkodeService = new LandkodeService();

    @Test
    public void hentIso3_forventIso2() throws LandkodeIkkeFunnetException {
        assertEquals(landkodeService.getLandkodeIso2("NOR"), "NO");
        assertEquals(landkodeService.getLandkodeIso2("SWE"), "SE");
        assertEquals(landkodeService.getLandkodeIso2("DNK"), "DK");
    }

    @Test(expected = LandkodeIkkeFunnetException.class)
    public void hentIso3_forventLandkodeIkkeFunnetException() throws LandkodeIkkeFunnetException {
        landkodeService.getLandkodeIso2("ABC");
    }
}
