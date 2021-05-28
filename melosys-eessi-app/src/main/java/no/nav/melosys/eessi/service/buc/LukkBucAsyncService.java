package no.nav.melosys.eessi.service.buc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LukkBucAsyncService {

    private final LukkBucService lukkBucService;

    public LukkBucAsyncService(LukkBucService lukkBucService) {
        this.lukkBucService = lukkBucService;
    }

    @Async
    public void forsøkLukkBUCAsync(String rinaSaksnummer) {
        try {
            lukkBucService.forsøkLukkBuc(rinaSaksnummer);
        } catch (Exception e) {
            log.error("Feil ved forsøk på lukking av rina-sak {}", rinaSaksnummer, e);
        }
    }
}
