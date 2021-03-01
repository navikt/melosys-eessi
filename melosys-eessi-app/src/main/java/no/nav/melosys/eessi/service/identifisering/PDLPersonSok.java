package no.nav.melosys.eessi.service.identifisering;

import no.nav.melosys.eessi.integration.pdl.PDLService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("pdl")
public class PDLPersonSok extends PersonSok {

    PDLPersonSok(PDLService pdlService) {
        super(pdlService);
    }
}
