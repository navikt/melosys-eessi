package no.nav.melosys.eessi.service.sending;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.identifisering.BucIdentifisertService;
import no.nav.melosys.eessi.identifisering.PersonIdentifisering;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.BucIdentifiseringOppg;
import no.nav.melosys.eessi.models.SedSendtHendelse;
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository;
import no.nav.melosys.eessi.repository.SedSendtHendelseRepository;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.oppgave.OppgaveService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SedSendtService {

    private final BucIdentifiseringOppgRepository bucIdentifiseringOppgRepository;
    private final OppgaveService oppgaveService;
    private final SedSendtHendelseRepository sedSendtHendelseRepository;


}
