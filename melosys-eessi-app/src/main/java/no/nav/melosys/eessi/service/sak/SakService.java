package no.nav.melosys.eessi.service.sak;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.integration.sak.SakConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SakService {

    private final SakConsumer sakConsumer;

    @Autowired
    public SakService(SakConsumer sakConsumer) {
        this.sakConsumer = sakConsumer;
    }

    public Sak hentsak(Long id) {
        return sakConsumer.getSak(Long.toString(id));
    }

}
