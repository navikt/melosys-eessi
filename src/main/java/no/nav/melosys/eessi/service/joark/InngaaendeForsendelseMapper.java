package no.nav.melosys.eessi.service.joark;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import no.nav.dok.tjenester.mottainngaaendeforsendelse.DokumentInfoHoveddokument;
import no.nav.dok.tjenester.mottainngaaendeforsendelse.DokumentVariant;
import no.nav.dok.tjenester.mottainngaaendeforsendelse.ForsendelseInformasjon;
import no.nav.dok.tjenester.mottainngaaendeforsendelse.MottaInngaaendeForsendelseRequest;
import no.nav.eessi.basis.SedMottatt;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.service.dokkat.DokkatSedInfo;
import static no.nav.melosys.eessi.service.joark.JournalpostUtils.organisasjon;
import static no.nav.melosys.eessi.service.joark.JournalpostUtils.person;

class InngaaendeForsendelseMapper {

    private static final String MOTTAKS_KANAL = "EESSI";

    static MottaInngaaendeForsendelseRequest createMottaInngaaendeForsendelseRequest(
            String aktoerId, SedMottatt sedMottatt, Sak sak, DokkatSedInfo dokkatSedInfo, ParticipantInfo senderInfo, byte[] pdf) {

        return new MottaInngaaendeForsendelseRequest()
                .withForsokEndeligJF(Boolean.FALSE)
                .withForsendelseInformasjon(forsendelseInformasjon(aktoerId, sedMottatt, sak, dokkatSedInfo, senderInfo))
                .withDokumentInfoHoveddokument(hoveddokument(dokkatSedInfo, pdf));
    }

    private static ForsendelseInformasjon forsendelseInformasjon(
            String aktoerId, SedMottatt sedMottatt, Sak sak, DokkatSedInfo dokkatSedInfo, ParticipantInfo senderInfo) {

        return new ForsendelseInformasjon()
                        .withBruker(person(aktoerId))
                        .withAvsender(organisasjon(senderInfo.getId(), senderInfo.getName()))
                        .withTema(sak.getTema())
                        .withKanalReferanseId(sedMottatt.getSedId())
                        .withForsendelseMottatt(Date.from(Instant.now()))
                        .withForsendelseInnsendt(Date.from(Instant.now()))
                        .withMottaksKanal(MOTTAKS_KANAL)
                        .withTittel(dokkatSedInfo.getDokumentTittel());
    }

    private static DokumentInfoHoveddokument hoveddokument(DokkatSedInfo dokkatSedInfo, byte[] pdf) {
        return new DokumentInfoHoveddokument()
                .withDokumentTypeId(dokkatSedInfo.getDokumenttypeId())
                .withDokumentVariant(Collections.singletonList(new DokumentVariant()
                        .withArkivFilType(DokumentVariant.ArkivFilType.PDFA)
                        .withVariantFormat(DokumentVariant.VariantFormat.ARKIV)
                        .withDokument(pdf)));
    }
}
