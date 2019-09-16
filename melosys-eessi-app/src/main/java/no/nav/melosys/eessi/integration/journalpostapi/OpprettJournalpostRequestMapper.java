package no.nav.melosys.eessi.integration.journalpostapi;

import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.service.dokkat.DokkatSedInfo;
import org.springframework.util.StringUtils;
import static no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostRequest.*;

@Slf4j
public class OpprettJournalpostRequestMapper {

    private static final EnumSet<SedType> TEMA_UFM_SEDTYPER = EnumSet.of(
            SedType.A001, SedType.A003, SedType.A009, SedType.A010
    );

    private OpprettJournalpostRequestMapper() {
    }

    public static OpprettJournalpostRequest opprettInngaaendeJournalpost(final SedHendelse sedHendelse,
            final SedMedVedlegg sedMedVedlegg,
            final Sak sak,
            final DokkatSedInfo dokkatSedInfo) {
        return opprettJournalpostRequest(JournalpostType.INNGAAENDE, sedHendelse, sedMedVedlegg, sak, dokkatSedInfo);
    }

    public static OpprettJournalpostRequest opprettUtgaaendeJournalpost(final SedHendelse sedHendelse,
            final SedMedVedlegg sedMedVedlegg,
            final Sak sak,
            final DokkatSedInfo dokkatSedInfo) {
        return opprettJournalpostRequest(JournalpostType.UTGAAENDE, sedHendelse, sedMedVedlegg, sak, dokkatSedInfo);
    }


    private static OpprettJournalpostRequest opprettJournalpostRequest(final JournalpostType journalpostType,
            final SedHendelse sedHendelse,
            final SedMedVedlegg sedMedVedlegg,
            final Sak sak,
            final DokkatSedInfo dokkatSedInfo) {

        return OpprettJournalpostRequest.builder()
                .avsenderMottaker(getAvsenderMottaker(journalpostType, sedHendelse))
                .behandlingstema(dokkatSedInfo.getBehandlingstema())
                .bruker(!StringUtils.isEmpty(sedHendelse.getNavBruker()) ? lagBruker(sedHendelse.getNavBruker()) : null)
                .dokumenter(dokumenter(sedHendelse.getSedType(), sedMedVedlegg, dokkatSedInfo))
                .eksternReferanseId(sedHendelse.getSedId())
                .journalfoerendeEnhet("4530")
                .journalpostType(journalpostType)
                .kanal("EESSI")
                .sak(sak != null ? OpprettJournalpostRequest.Sak.builder().arkivsaksnummer(sak.getId()).build() : null)
                .tema(sak != null ? sak.getTema() : temaFraSedType(sedHendelse.getSedType()))
                .tittel(dokkatSedInfo.getDokumentTittel())
                .tilleggsopplysninger(Arrays.asList(
                        Tilleggsopplysning.builder().nokkel("rinaSakId").verdi(sedHendelse.getRinaSakId()).build(),
                        Tilleggsopplysning.builder().nokkel("rinaDokumentId").verdi(sedHendelse.getRinaDokumentId()).build()
                        ))
                .build();
    }

    private static String temaFraSedType(String sedType) {
        SedType sedTypeEnum = SedType.valueOf(sedType);
        return TEMA_UFM_SEDTYPER.contains(sedTypeEnum) ? "UFM" : "MED";
    }

    private static Bruker lagBruker(final String fnr) {
        return Bruker.builder()
                .id(fnr)
                .idType(BrukerIdType.FNR)
                .build();
    }

    private static AvsenderMottaker getAvsenderMottaker(final JournalpostType type,
            final SedHendelse sedHendelse) {

        return AvsenderMottaker.builder()
                .id(type == JournalpostType.UTGAAENDE ? sedHendelse.getMottakerId() : sedHendelse.getAvsenderId())
                .navn(type == JournalpostType.UTGAAENDE ? sedHendelse.getMottakerNavn() : sedHendelse.getAvsenderNavn())
                .idType(AvsenderMottaker.IdType.UTL_ORG)
                .build();
    }

    private static List<Dokument> dokumenter(final String sedType, final SedMedVedlegg sedMedVedlegg,
            final DokkatSedInfo dokkatSedInfo) {
        final List<Dokument> dokumenter = new ArrayList<>();

        dokumenter.add(dokument(sedType, dokkatSedInfo.getDokumentTittel(), JournalpostFiltype.PDFA, sedMedVedlegg.getSed().getInnhold()));
        dokumenter.addAll(vedlegg(sedType, sedMedVedlegg.getVedleggListe()));
        return dokumenter;
    }

    private static Dokument dokument(final String sedType, final String filnavn, JournalpostFiltype journalpostFiltype, byte[] innhold) {
        return Dokument.builder()
                .dokumentvarianter(Collections.singletonList(DokumentVariant.builder()
                        .filtype(journalpostFiltype)
                        .fysiskDokument(innhold)
                        .variantformat("ARKIV")
                        .build()))
                .sedType(sedType)
                .tittel(filnavn)
                .build();
    }

    private static List<Dokument> vedlegg(final String sedType, final List<SedMedVedlegg.BinaerFil> vedleggListe) {
        return vedleggListe.stream()
                .filter(b -> {
                    if (!JournalpostFiltype.filnavn(b.getFilnavn()).isPresent()) {
                        log.warn(
                                "fant vedlegg i lista med vedlegg som har en filtype som ikke er støttet av arkivet, filnavn={}, fjerner fra lista.",
                                b.getFilnavn());
                    }
                    return JournalpostFiltype.filnavn(b.getFilnavn()).isPresent();
                }).map(
                        b -> dokument(sedType, b.getFilnavn(),
                                JournalpostFiltype.filnavn(b.getFilnavn()).orElse(null),
                                b.getInnhold()))
                .collect(Collectors.toList());
    }
}
