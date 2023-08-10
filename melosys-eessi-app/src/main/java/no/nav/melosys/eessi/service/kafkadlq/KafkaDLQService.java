package no.nav.melosys.eessi.service.kafkadlq;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.identifisering.OppgaveKafkaAivenRecord;
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
        sedMottattHendelseDLQ.setQueueType(QueueType.SED_MOTTATT_HENDELSE);
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
        sedSendtHendelseDLQ.setQueueType(QueueType.SED_SENDT_HENDELSE);
        sedSendtHendelseDLQ.setTidRegistrert(LocalDateTime.now());
        sedSendtHendelseDLQ.setSisteFeilmelding(feilmelding);
        sedSendtHendelseDLQ.setId(randomUuid);
        kafkaDLQRepository.save(sedSendtHendelseDLQ);
    }

    @Transactional
    public void lagreOppgaveEndretHendelse(OppgaveKafkaAivenRecord oppgaveHendelse, String feilmelding) {
        UUID randomUuid = UUID.randomUUID();
        log.info("Lagrer oppgaveHendelse på DLQ, oppgave={}, uuid={}", oppgaveHendelse.oppgave().oppgaveId(), randomUuid);

        OppgaveHendelseAivenKafkaDLQ oppgaveHendelseDLQ = new OppgaveHendelseAivenKafkaDLQ();
        oppgaveHendelseDLQ.setOppgaveEndretHendelse(oppgaveHendelse);
        oppgaveHendelseDLQ.setQueueType(QueueType.OPPGAVE_HENDELSE_AIVEN);
        oppgaveHendelseDLQ.setTidRegistrert(LocalDateTime.now());
        oppgaveHendelseDLQ.setSisteFeilmelding(feilmelding);
        oppgaveHendelseDLQ.setId(randomUuid);
        kafkaDLQRepository.save(oppgaveHendelseDLQ);
    }

    @Transactional
    public void lagreOppgaveEndretHendelseGammel(OppgaveEndretHendelseGammel oppgaveEndretHendelseGammel, String feilmelding) {
        UUID randomUuid = UUID.randomUUID();
        log.info("Lagrer oppgaveEndretHendelse på DLQ, oppgave={}, uuid={}", oppgaveEndretHendelse.oppgave().oppgaveId(), randomUuid);

        OppgaveEndretHendelseKafkaDLQ oppgaveEndretHendelseDLQ = new OppgaveEndretHendelseKafkaDLQ();
        oppgaveEndretHendelseDLQ.setOppgaveEndretHendelse(oppgaveEndretHendelse);
        oppgaveEndretHendelseDLQ.setQueueType(QueueType.OPPGAVE_ENDRET_HENDELSE_AIVEN);
        oppgaveEndretHendelseDLQ.setTidRegistrert(LocalDateTime.now());
        oppgaveEndretHendelseDLQ.setSisteFeilmelding(feilmelding);
        oppgaveEndretHendelseDLQ.setId(randomUuid);
        kafkaDLQRepository.save(oppgaveEndretHendelseDLQ);
    }

    @Transactional
    public void rekjørAlleKafkaMeldinger() {
        kafkaDLQRepository.findAll().forEach(
            kafkaMelding -> {
                try {
                    rekjørKafkaMelding(kafkaMelding.getId());
                } catch (Exception e) {
                    log.error("Rekjøring av melding feilet, uuid=" + kafkaMelding.getId().toString(), e);
                }
            }
        );
    }

    public void rekjørKafkaMelding(UUID uuid) {
        KafkaDLQ kafkaDLQMelding = kafkaDLQRepository.findById(uuid).orElseThrow(
            () -> new NotFoundException("Kunne ikke finne KafkaDLQ-melding basert, uuid=" + uuid)
        );

        if (kafkaDLQMelding instanceof SedMottattHendelseKafkaDLQ sedMottattHendelse) {
            rekjorSedMottattHendelse(sedMottattHendelse);
        } else if (kafkaDLQMelding instanceof SedSendtHendelseKafkaDLQ sedSendtHendelse) {
            rekjorSedSendtHendelse(sedSendtHendelse);
        } else if (kafkaDLQMelding instanceof OppgaveEndretHendelseKafkaDLQ oppgaveEndretHendelse) {
            rekjorOppgaveEndretHendelse(oppgaveEndretHendelse);
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
        } catch (Exception e) {
            sedMottattHendelseKafkaDLQ.setTidSistRekjort(LocalDateTime.now());
            sedMottattHendelseKafkaDLQ.setSisteFeilmelding(e.getMessage());
            sedMottattHendelseKafkaDLQ.økAntallRekjøringerMed1();
            kafkaDLQRepository.save(sedMottattHendelseKafkaDLQ);
            throw e;
        } finally {
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
            sedSendtHendelseDLQ.setTidSistRekjort(LocalDateTime.now());
            sedSendtHendelseDLQ.setSisteFeilmelding(e.getMessage());
            sedSendtHendelseDLQ.økAntallRekjøringerMed1();
            kafkaDLQRepository.save(sedSendtHendelseDLQ);
            throw e;
        } finally {
            remove(SED_ID);
        }
    }

    private void rekjorOppgaveHendelse(OppgaveHendelseAivenKafkaDLQ oppgaveHendelseAivenKafkaDLQ) {
        putToMDC(CORRELATION_ID, UUID.randomUUID().toString());

        log.info("Rekjører melding om oppgave endret: {}, uuid: {}",
            oppgaveHendelseAivenKafkaDLQ.getOppgaveEndretHendelse(),
            oppgaveHendelseAivenKafkaDLQ.getId());

        try {
            oppgaveEndretService.behandleOppgaveEndretHendelse(oppgaveHendelseAivenKafkaDLQ.getOppgaveEndretHendelse());
            kafkaDLQRepository.delete(oppgaveHendelseAivenKafkaDLQ);
        } catch (Exception e) {
            oppgaveHendelseAivenKafkaDLQ.setTidSistRekjort(LocalDateTime.now());
            oppgaveHendelseAivenKafkaDLQ.setSisteFeilmelding(e.getMessage());
            oppgaveHendelseAivenKafkaDLQ.økAntallRekjøringerMed1();
            kafkaDLQRepository.save(oppgaveHendelseAivenKafkaDLQ);
            throw e;
        } finally {
            remove(CORRELATION_ID);
        }
    }


    public List<KafkaDLQ> hentFeiledeKafkaMeldinger() {
        return kafkaDLQRepository.findAll();
    }
}
