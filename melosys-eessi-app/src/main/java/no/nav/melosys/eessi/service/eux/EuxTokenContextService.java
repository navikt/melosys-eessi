package no.nav.melosys.eessi.service.eux;

import no.nav.melosys.eessi.integration.eux.rina_api.EuxConsumer;
import no.nav.melosys.eessi.integration.eux.rina_api.EuxRinasakerConsumer;
import no.nav.melosys.eessi.metrikker.BucMetrikker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("tokenContext")
public class EuxTokenContextService extends EuxService {

    public EuxTokenContextService(@Qualifier("tokenContext") EuxConsumer euxConsumer,
                                  EuxRinasakerConsumer euxRinasakerConsumer,
                                  BucMetrikker bucMetrikker) {
        super(euxConsumer, bucMetrikker, euxRinasakerConsumer);
    }
}
