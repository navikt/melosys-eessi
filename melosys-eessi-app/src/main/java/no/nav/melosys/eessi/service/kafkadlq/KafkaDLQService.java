package no.nav.melosys.eessi.service.kafkadlq;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.identifisering.OppgaveEndretHendelse;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.kafkadlq.*;
import no.nav.melosys.eessi.repository.KafkaDLQRepository;
import no.nav.melosys.eessi.service.joark.OpprettUtgaaendeJournalpostService;
import no.nav.melosys.eessi.service.mottak.SedMottakService;
import no.nav.melosys.eessi.service.oppgave.OppgaveEndretService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static no.nav.melosys.eessi.config.MDCOperations.*;
import static no.nav.melosys.eessi.config.MDCOperations.SED_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaDLQService {

    private final KafkaDLQRepository kafkaDLQRepository;
    private final SedMottakService sedMottakService;
    private final OpprettUtgaaendeJournalpostService opprettUtgaaendeJournalpostService;
    private final OppgaveEndretService oppgaveEndretService;

    @Transactional
    public void lagreNySedMottattHendelse(SedHendelse sedHendelse, String feilmelding) {
        UUID randomUuid = UUID.randomUUID();
        log.info("Lagrer sedMottattHendelse på DLQ, sedId={}, uuid={}", sedHendelse.getSedId(), randomUuid);

        SedMottattHendelseKafkaDLQ sedMottattHendelseDLQ = new SedMottattHendelseKafkaDLQ();
        sedMottattHendelseDLQ.setSedMottattHendelse(sedHendelse);
        sedMottattHendelseDLQ.setKoType(KoType.SED_MOTTATT_HENDELSE);
        sedMottattHendelseDLQ.setTidRegistrert(LocalDateTime.now());
        sedMottattHendelseDLQ.setSisteFeilmelding(feilmelding);
        sedMottattHendelseDLQ.setId(UUID.randomUUID());
        kafkaDLQRepository.save(sedMottattHendelseDLQ);
    }

    @Transactional
    public void lagreSedSendtHendelse(SedHendelse sedHendelse, String feilmelding) {
        UUID randomUuid = UUID.randomUUID();
        log.info("Lagrer sedSendtHendelse på DLQ, sedId={}, uuid={}", sedHendelse.getSedId(), randomUuid);

        SedSendtHendelseKafkaDLQ sedSendtHendelseDLQ = new SedSendtHendelseKafkaDLQ();
        sedSendtHendelseDLQ.setSedSendtHendelse(sedHendelse);
        sedSendtHendelseDLQ.setKoType(KoType.SED_SENDT_HENDELSE);
        sedSendtHendelseDLQ.setTidRegistrert(LocalDateTime.now());
        sedSendtHendelseDLQ.setSisteFeilmelding(feilmelding);
        sedSendtHendelseDLQ.setId(randomUuid);
        kafkaDLQRepository.save(sedSendtHendelseDLQ);
    }

    @Transactional
    public void lagreOppgaveEndretHendelse(OppgaveEndretHendelse oppgaveEndretHendelse, String feilmelding) {
        UUID randomUuid = UUID.randomUUID();
        log.info("Lagrer oppgaveEndretHendelse på DLQ, oppgave={}, uuid={}", oppgaveEndretHendelse.getId(), randomUuid);

        OppgaveEndretHendelseKafkaDLQ oppgaveEndretHendelseDLQ = new OppgaveEndretHendelseKafkaDLQ();
        oppgaveEndretHendelseDLQ.setOppgaveEndretHendelse(oppgaveEndretHendelse);
        oppgaveEndretHendelseDLQ.setKoType(KoType.OPPGAVE_ENDRET_HENDELSE);
        oppgaveEndretHendelseDLQ.setTidRegistrert(LocalDateTime.now());
        oppgaveEndretHendelseDLQ.setSisteFeilmelding(feilmelding);
        oppgaveEndretHendelseDLQ.setId(randomUuid);
        kafkaDLQRepository.save(oppgaveEndretHendelseDLQ);
    }

    @Transactional
    public void rekjorKafkaMelding(UUID uuid) {
        KafkaDLQ kafkaDLQMelding = kafkaDLQRepository.findById(uuid).orElseThrow(
            () -> new NotFoundException("Kunne ikke finne KafkaDLQ-melding basert, uuid=" + uuid)
        );

        if (kafkaDLQMelding instanceof SedMottattHendelseKafkaDLQ) {
            rekjorSedMottattHendelse((SedMottattHendelseKafkaDLQ) kafkaDLQMelding);
        } else if (kafkaDLQMelding instanceof SedSendtHendelseKafkaDLQ) {
            rekjorSedSendtHendelse((SedSendtHendelseKafkaDLQ) kafkaDLQMelding);
        } else if (kafkaDLQMelding instanceof OppgaveEndretHendelseKafkaDLQ) {
            rekjorOppgaveEndretHendelse((OppgaveEndretHendelseKafkaDLQ) kafkaDLQMelding);
        }
    }

    private void rekjorSedMottattHendelse(SedMottattHendelseKafkaDLQ sedMottattHendelseKafkaDLQ) {
        SedHendelse sedHendelse = sedMottattHendelseKafkaDLQ.getSedMottattHendelse();

        putToMDC(SED_ID, sedHendelse.getSedId());
        putToMDC(CORRELATION_ID, UUID.randomUUID().toString());
        log.info("Mottatt melding om sed mottatt: {}, uuid: {}", sedHendelse, sedMottattHendelseKafkaDLQ.getId());

        try {
            sedMottakService.behandleSedMottakHendelse(sedHendelse);
            kafkaDLQRepository.delete(sedMottattHendelseKafkaDLQ);
        }
        catch (Exception e) {
            sedMottattHendelseKafkaDLQ.setSisteFeilmelding(e.getMessage());
            sedMottattHendelseKafkaDLQ.økAntallRekjøringerMed1();
            kafkaDLQRepository.save(sedMottattHendelseKafkaDLQ);
            throw e;
        }
        finally {
            remove(SED_ID);
            remove(CORRELATION_ID);
        }
    }

    private void rekjorSedSendtHendelse(SedSendtHendelseKafkaDLQ sedSendtHendelseDLQ) {
        SedHendelse sedHendelse = sedSendtHendelseDLQ.getSedSendtHendelse();

        putToMDC(SED_ID, sedHendelse.getSedId());
        log.info("Rekjører melding om sed sendt: {}, uuid: {}", sedHendelse, sedSendtHendelseDLQ.getId());

        try {
            opprettUtgaaendeJournalpostService.behandleSedSendtHendelse(sedHendelse);
            kafkaDLQRepository.delete(sedSendtHendelseDLQ);
        } catch (Exception e) {
            sedSendtHendelseDLQ.setSisteFeilmelding(e.getMessage());
            sedSendtHendelseDLQ.økAntallRekjøringerMed1();
            kafkaDLQRepository.save(sedSendtHendelseDLQ);
            throw e;
        }
        finally {
            remove(SED_ID);
        }
    }

    private void rekjorOppgaveEndretHendelse(OppgaveEndretHendelseKafkaDLQ oppgaveEndretHendelseKafkaDLQ) {
        putToMDC(CORRELATION_ID, UUID.randomUUID().toString());

        log.info("Rekjører melding om oppgave endret: {}, uuid: {}",
            oppgaveEndretHendelseKafkaDLQ.getOppgaveEndretHendelse(),
            oppgaveEndretHendelseKafkaDLQ.getId());

        try {
            oppgaveEndretService.behandleOppgaveEndretHendelse(oppgaveEndretHendelseKafkaDLQ.getOppgaveEndretHendelse());
            kafkaDLQRepository.delete(oppgaveEndretHendelseKafkaDLQ);
        }
        catch (Exception e) {
            oppgaveEndretHendelseKafkaDLQ.setSisteFeilmelding(e.getMessage());
            oppgaveEndretHendelseKafkaDLQ.økAntallRekjøringerMed1();
            kafkaDLQRepository.save(oppgaveEndretHendelseKafkaDLQ);
            throw e;
        }finally {
            remove(CORRELATION_ID);
        }
    }


    public List<KafkaDLQ> hentFeiledeKafkaMeldinger() {
        return kafkaDLQRepository.findAll();
    }
}
