package no.nav.melosys.eessi.service.identifisering;

import no.nav.melosys.eessi.integration.tps.TpsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@Qualifier("tps")
class TpsPersonSok extends PersonSok {

    TpsPersonSok(TpsService tpsService) {
        super(tpsService);
    }
}
