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

    /*
    Async for at ekstern tjeneste ikke skal trenge å vente på resultat herfra.
    Blir kalt eksternt for å indikere at en tilhørende behandling er avsluttet, og at man kan anse utveksling som ferdig.
    Kan fortsatt ikke garantere at RINA har tilgjengeliggjort lukking av BUCen (create X001)
     */
    @Async
    public void forsøkLukkBUCAsync(String rinaSaksnummer) {
        try {
            lukkBucService.forsøkLukkBuc(rinaSaksnummer);
        } catch (Exception e) {
            log.error("Feil ved forsøk på lukking av rina-sak {}", rinaSaksnummer, e);
        }
    }
}
