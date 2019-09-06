package no.nav.melosys.eessi.metrikker;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import static no.nav.melosys.eessi.metrikker.MetrikkerNavn.*;

@Service
public class MetrikkerRegistrering {

    private final MeterRegistry meterRegistry;

    public MetrikkerRegistrering(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void personIdentifisert(boolean personFunnet, SedHendelse sedHendelse) {
        inkrementer(IDENTIFISERING_FUNNET,
                KEY_PERSON_IDENTIFISERT, String.valueOf(personFunnet),
                KEY_BUCTYPE, sedHendelse.getBucType(),
                KEY_SEDTYPE, sedHendelse.getSedType(),
                KEY_AVSENDERLAND, splitInstitusjonId(sedHendelse.getAvsenderId()));
    }

    public void sedMottatt(SedHendelse sedMottatt) {
        inkrementer(SED_MOTTATT,
                KEY_SEDTYPE, sedMottatt.getSedType(),
                KEY_BUCTYPE, sedMottatt.getBucType(),
                KEY_AVSENDERLAND, splitInstitusjonId(sedMottatt.getAvsenderId()),
                KEY_SED_VERSJON, sedMottatt.getRinaDokumentVersjon(),
                KEY_RINASAKSNUMMER, sedMottatt.getRinaSakId());
    }

    public void sedSendt(SedHendelse sedMottatt) {
        inkrementer(SED_SENDT,
                KEY_SEDTYPE, sedMottatt.getSedType(),
                KEY_BUCTYPE, sedMottatt.getBucType(),
                KEY_MOTTAKERLAND, splitInstitusjonId(sedMottatt.getMottakerId()),
                KEY_SED_VERSJON, sedMottatt.getRinaDokumentVersjon(),
                KEY_RINASAKSNUMMER, sedMottatt.getRinaSakId());
    }

    public void bucOpprettet(String bucType) {
        inkrementer(BUC_OPPRETTET,
                KEY_BUCTYPE, bucType);
    }

    public void bucLukket(String bucType) {
        inkrementer(BUC_LUKKET,
                KEY_BUCTYPE, bucType);
    }

    public void journalpostInngaaendeOpprettet() {
        inkrementer(JOURNALPOST_INNGAAENDE_OPPRETTET);
    }

    public void journalpostUtgaaendeOpprettet(boolean ferdigstilt) {
        journalpostUtgaaendeOpprettet();
        if (ferdigstilt) {
            journalpostUtgaaendeFerdigstilt();
        }
    }

    private void journalpostUtgaaendeOpprettet() {
        inkrementer(JOURNALPOST_UTGAAENDE_OPPRETTET);
    }

    private void journalpostUtgaaendeFerdigstilt() {
        inkrementer(JOURNALPOST_UTGAAENDE_FERDIGSTILT);
    }

    private void inkrementer(String navn, String... tags) {
        meterRegistry.counter(navn, tags).increment();
    }

    private static String splitInstitusjonId(String institusjonId) {
        if (StringUtils.isEmpty(institusjonId)) {
            return "ukjent";
        }
        return institusjonId.split(":")[0];
    }
}
