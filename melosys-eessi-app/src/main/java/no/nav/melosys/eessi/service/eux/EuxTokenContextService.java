package no.nav.melosys.eessi.service.eux;

import no.finn.unleash.Unleash;
import no.nav.melosys.eessi.integration.eux.rina_api.EuxConsumer;
import no.nav.melosys.eessi.metrikker.BucMetrikker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Qualifier("tokenContext")
public class EuxTokenContextService extends EuxService {

    public EuxTokenContextService(@Qualifier("tokenContext") EuxConsumer euxConsumer,
                                  BucMetrikker bucMetrikker,
                                  Unleash unleash) {
        super(euxConsumer, bucMetrikker, unleash);
    }
}
