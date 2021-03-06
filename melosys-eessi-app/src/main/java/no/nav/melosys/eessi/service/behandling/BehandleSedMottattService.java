package no.nav.melosys.eessi.service.behandling;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.identifisering.PersonIdentifisering;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.integration.journalpostapi.SedAlleredeJournalførtException;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiProducer;
import no.nav.melosys.eessi.models.SedKontekst;
import no.nav.melosys.eessi.models.SedMottatt;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import no.nav.melosys.eessi.service.joark.SakInformasjon;
import no.nav.melosys.eessi.service.oppgave.OppgaveService;
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapper;
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Deprecated(forRemoval = true)
public class BehandleSedMottattService {

    private final OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;
    private final EuxService euxService;
    private final PersonFasade personFasade;
    private final MelosysEessiProducer melosysEessiProducer;
    private final PersonIdentifisering personIdentifisering;
    private final OppgaveService oppgaveService;
    private final MelosysEessiMeldingMapperFactory melosysEessiMeldingMapperFactory;

    @Autowired
    public BehandleSedMottattService(
            OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService,
            EuxService euxService,
            PersonFasade personFasade,
            MelosysEessiProducer melosysEessiProducer,
            PersonIdentifisering personIdentifisering,
            OppgaveService oppgaveService, MelosysEessiMeldingMapperFactory melosysEessiMeldingMapperFactory) {
        this.opprettInngaaendeJournalpostService = opprettInngaaendeJournalpostService;
        this.euxService = euxService;
        this.personFasade = personFasade;
        this.melosysEessiProducer = melosysEessiProducer;
        this.personIdentifisering = personIdentifisering;
        this.oppgaveService = oppgaveService;
        this.melosysEessiMeldingMapperFactory = melosysEessiMeldingMapperFactory;
    }

    public void behandleSed(SedMottatt sedMottatt) {
        SedKontekst kontekst = sedMottatt.getSedKontekst();
        SED sed = euxService.hentSed(sedMottatt.getSedHendelse().getRinaSakId(),
                sedMottatt.getSedHendelse().getRinaDokumentId());

        if (!kontekst.isForsoktIdentifisert()) {
            identifiserPerson(sedMottatt, sed);
        }

        if (!kontekst.journalpostOpprettet()) {
            try {
                opprettJournalpost(sedMottatt);
            } catch (SedAlleredeJournalførtException e) {
                log.info("Inngående SED {} allerede journalført", e.getSedID());
                sedMottatt.setFerdig(true);
                return;
            }
        }

        if (kontekst.personErIdentifisert()) {
            if (!kontekst.isPublisertKafka()) {
                publiserMelding(sedMottatt, sed);
            }
        } else {
            if (!kontekst.identifiseringsOppgaveOpprettet()) {
                opprettOppgaveIdentifisering(sedMottatt);
            }
        }
    }

    private void identifiserPerson(SedMottatt sedMottatt, SED sed) {
        log.info("Søker etter person for SED {}", sedMottatt.getSedHendelse().getRinaDokumentId());
        personIdentifisering.identifiserPerson(sedMottatt.getSedHendelse().getRinaSakId(), sed)
                .ifPresent(s -> sedMottatt.getSedKontekst().setNavIdent(s));
        sedMottatt.getSedKontekst().setForsoktIdentifisert(true);
        sedMottatt.getSedHendelse().setNavBruker(sedMottatt.getSedKontekst().getNavIdent());
    }

    private void opprettJournalpost(SedMottatt sedMottatt) {
        log.info("Oppretter journalpost for SED {}", sedMottatt.getSedHendelse().getRinaDokumentId());
        SedMedVedlegg sedMedVedlegg = euxService.hentSedMedVedlegg(
                sedMottatt.getSedHendelse().getRinaSakId(), sedMottatt.getSedHendelse().getRinaDokumentId()
        );

        if (sedMottatt.getSedKontekst().personErIdentifisert()) {
            SakInformasjon sakInformasjon = opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(
                    sedMottatt.getSedHendelse(), sedMedVedlegg, sedMottatt.getSedKontekst().getNavIdent());
            sedMottatt.getSedKontekst().setJournalpostID(sakInformasjon.getJournalpostId());
            sedMottatt.getSedKontekst().setDokumentID(sakInformasjon.getDokumentId());
        } else {
            String journalpostID = opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(
                    sedMottatt.getSedHendelse(), sedMedVedlegg, null);
            sedMottatt.getSedKontekst().setJournalpostID(journalpostID);
        }
    }

    private void opprettOppgaveIdentifisering(SedMottatt sedMottatt) {
        log.info("Oppretter oppgave til ID og fordeling for SED {}", sedMottatt.getSedHendelse().getRinaDokumentId());
        String oppgaveID = oppgaveService.opprettOppgaveTilIdOgFordeling(
                sedMottatt.getSedKontekst().getJournalpostID(), sedMottatt.getSedHendelse().getSedType(), sedMottatt.getSedHendelse().getRinaSakId()
        );
        sedMottatt.getSedKontekst().setOppgaveID(oppgaveID);
    }

    private void publiserMelding(SedMottatt sedMottatt, SED sed) {
        log.info("Publiserer melding om mottatt sed på kafka for SED {}", sedMottatt.getSedHendelse().getRinaDokumentId());
        MelosysEessiMeldingMapper mapper = melosysEessiMeldingMapperFactory.getMapper(SedType.valueOf(sed.getSedType()));
        if (mapper == null) {
            throw new IllegalStateException("Mapper for kafka-publisering er null!");
        }

        SedHendelse sedHendelse = sedMottatt.getSedHendelse();
        String aktoerID = personFasade.hentAktoerId(sedMottatt.getSedKontekst().getNavIdent());
        boolean sedErEndring = euxService.sedErEndring(sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId());

        melosysEessiProducer.publiserMelding(
                mapper.map(aktoerID, sed, sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId(),
                        sedHendelse.getSedType(), sedHendelse.getBucType(), sedHendelse.getAvsenderId(), sedHendelse.getLandkode(),
                        sedMottatt.getSedKontekst().getJournalpostID(), sedMottatt.getSedKontekst().getDokumentID(),
                        sedMottatt.getSedKontekst().getGsakSaksnummer(), sedErEndring, sedHendelse.getRinaDokumentVersjon())
        );

        sedMottatt.getSedKontekst().setPublisertKafka(Boolean.TRUE);
    }
}
