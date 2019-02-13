package no.nav.melosys.eessi.service.gsak;

import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.integration.gsak.SakConsumer;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GsakService {

    private final SakConsumer sakConsumer;

    @Autowired
    public GsakService(SakConsumer sakConsumer) {
        this.sakConsumer = sakConsumer;
    }

    public Sak getSak(Long id) throws IntegrationException {
        return sakConsumer.getSak(id);
    }
}
