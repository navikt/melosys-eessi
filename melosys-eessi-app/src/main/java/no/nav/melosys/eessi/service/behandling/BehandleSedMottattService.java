package no.nav.melosys.eessi.service.behandling;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiProducer;
import no.nav.melosys.eessi.kafka.producers.mapping.MelosysEessiMeldingMapper;
import no.nav.melosys.eessi.kafka.producers.mapping.MelosysEessiMeldingMapperFactory;
import no.nav.melosys.eessi.models.SedKontekst;
import no.nav.melosys.eessi.models.SedMottatt;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.identifisering.PersonIdentifiseringService;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import no.nav.melosys.eessi.service.joark.SakInformasjon;
import no.nav.melosys.eessi.service.oppgave.OppgaveService;
import no.nav.melosys.eessi.service.tps.TpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BehandleSedMottattService {

    private final OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;
    private final EuxService euxService;
    private final TpsService tpsService;
    private final MelosysEessiProducer melosysEessiProducer;
    private final PersonIdentifiseringService personIdentifiseringService;
    private final OppgaveService oppgaveService;

    @Autowired
    public BehandleSedMottattService(
            OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService,
            EuxService euxService,
            TpsService tpsService,
            MelosysEessiProducer melosysEessiProducer,
            PersonIdentifiseringService personIdentifiseringService,
            OppgaveService oppgaveService) {
        this.opprettInngaaendeJournalpostService = opprettInngaaendeJournalpostService;
        this.euxService = euxService;
        this.tpsService = tpsService;
        this.melosysEessiProducer = melosysEessiProducer;
        this.personIdentifiseringService = personIdentifiseringService;
        this.oppgaveService = oppgaveService;
    }

    public void behandleSed(SedMottatt sedMottatt) throws NotFoundException, IntegrationException {
        SedKontekst kontekst = sedMottatt.getSedKontekst();
        if (!kontekst.isForsoktIdentifisert()) {
            identifiserPerson(sedMottatt);
        }

        if (!kontekst.journalpostOpprettet()) {
            opprettJournalpost(sedMottatt);
        }

        if (kontekst.personErIdentifisert()) {
            if (!kontekst.isPublisertKafka()) {
                publiserMelding(sedMottatt);
            }
        } else {
            if (!kontekst.identifiseringsOppgaveOpprettet()) {
                opprettOppgaveIdentifisering(sedMottatt);
            }
        }
    }

    private void identifiserPerson(SedMottatt sedMottatt) throws IntegrationException, NotFoundException {
        SED sed = euxService.hentSed(sedMottatt.getSedHendelse().getRinaSakId(), sedMottatt.getSedHendelse().getRinaDokumentId());
        personIdentifiseringService.identifiserPerson(sedMottatt.getSedHendelse(), sed)
                .ifPresent(s -> sedMottatt.getSedKontekst().setNavIdent(s));
        sedMottatt.getSedKontekst().setForsoktIdentifisert(true);
    }

    private void opprettJournalpost(SedMottatt sedMottatt) throws IntegrationException {
        SedMedVedlegg sedMedVedlegg = euxService.hentSedMedVedlegg(
                sedMottatt.getSedHendelse().getRinaSakId(), sedMottatt.getSedHendelse().getRinaDokumentId()
        );

        if (sedMottatt.getSedKontekst().personErIdentifisert()) {
            SakInformasjon sakInformasjon = opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(
                    sedMottatt.getSedHendelse(), sedMedVedlegg);
            sedMottatt.getSedKontekst().setJournalpostID(sakInformasjon.getJournalpostId());
            sedMottatt.getSedKontekst().setDokumentID(sakInformasjon.getDokumentId());
        } else {
            String journalpostID = opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(
                    sedMottatt.getSedHendelse(), sedMedVedlegg);
            sedMottatt.getSedKontekst().setJournalpostID(journalpostID);
        }
    }

    private void opprettOppgaveIdentifisering(SedMottatt sedMottatt) throws IntegrationException {
        //TODO: opprett Oppgave til ID og fordeling. Oppretter jfr-oppgave for nå.
        String oppgaveID = oppgaveService.opprettJfrOppgave(sedMottatt.getSedKontekst().getJournalpostID());
        sedMottatt.getSedKontekst().setOppgaveID(oppgaveID);
    }

    private void publiserMelding(SedMottatt sedMottatt) throws IntegrationException, NotFoundException {
        SedHendelse sedHendelse = sedMottatt.getSedHendelse();
        SED sed = euxService.hentSed(sedHendelse.getRinaSakId(), sedHendelse.getRinaDokumentId());
        SedType sedType = SedType.valueOf(sed.getSed());
        String aktoerID = tpsService.hentAktoerId(sedMottatt.getSedKontekst().getNavIdent());
        MelosysEessiMeldingMapper mapper = MelosysEessiMeldingMapperFactory.getMapper(sedType);

        if (mapper == null) {
            throw new IllegalArgumentException("Mapper for kafka-publisering er null!");
        }

        boolean sedErEndring = euxService.sedErEndring(sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId());

        melosysEessiProducer.publiserMelding(
                mapper.map(aktoerID, sed, sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId(),
                        sedHendelse.getSedType(), sedHendelse.getBucType(), sedMottatt.getSedKontekst().getJournalpostID(),
                        sedMottatt.getSedKontekst().getDokumentID(), sedMottatt.getSedKontekst().getGsakSaksnummer(), sedErEndring)
        );

        sedMottatt.getSedKontekst().setPublisertKafka(Boolean.TRUE);
    }
}
