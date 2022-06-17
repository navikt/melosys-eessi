package no.nav.melosys.eessi.integration.journalpostapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.service.dokkat.DokkatSedInfo;

import static no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostRequest.*;
import static no.nav.melosys.eessi.service.sed.SedTypeTilTemaMapper.temaForSedType;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
public final class OpprettJournalpostRequestMapper {

    private OpprettJournalpostRequestMapper() {
    }

    public static OpprettJournalpostRequest opprettInngaaendeJournalpost(final SedHendelse sedHendelse,
                                                                         final SedMedVedlegg sedMedVedlegg,
                                                                         final Sak sak,
                                                                         final DokkatSedInfo dokkatSedInfo,
                                                                         final String personIdent) {
        return opprettJournalpostRequest(JournalpostType.INNGAAENDE, sedHendelse, sedMedVedlegg, sak, dokkatSedInfo, personIdent);
    }

    public static OpprettJournalpostRequest opprettUtgaaendeJournalpost(final SedHendelse sedHendelse,
                                                                        final SedMedVedlegg sedMedVedlegg,
                                                                        final Sak sak,
                                                                        final DokkatSedInfo dokkatSedInfo,
                                                                        final String personIdent) {
        return opprettJournalpostRequest(JournalpostType.UTGAAENDE, sedHendelse, sedMedVedlegg, sak, dokkatSedInfo, personIdent);
    }


    private static OpprettJournalpostRequest opprettJournalpostRequest(final JournalpostType journalpostType,
                                                                       final SedHendelse sedHendelse,
                                                                       final SedMedVedlegg sedMedVedlegg,
                                                                       final Sak sak,
                                                                       final DokkatSedInfo dokkatSedInfo,
                                                                       final String personIdent) {

        return OpprettJournalpostRequest.builder()
                .avsenderMottaker(getAvsenderMottaker(journalpostType, sedHendelse))
                .behandlingstema(dokkatSedInfo.getBehandlingstema())
                .bruker(isNotEmpty(personIdent) ? lagBruker(personIdent) : null)
                .dokumenter(dokumenter(sedHendelse.getSedType(), sedMedVedlegg, dokkatSedInfo))
                .eksternReferanseId(sedHendelse.getSedId())
                .journalfoerendeEnhet("4530")
                .journalpostType(journalpostType)
                .kanal("EESSI")
                .sak(sak != null ? OpprettJournalpostRequest.Sak.builder().arkivsaksnummer(sak.getId()).build() : null)
                .tema(sak != null ? sak.getTema() : temaForSedTypeOgJournalpostType(sedHendelse.getSedType(), journalpostType))
                .tittel(dokkatSedInfo.getDokumentTittel())
                .tilleggsopplysninger(Arrays.asList(
                        Tilleggsopplysning.builder().nokkel("rinaSakId").verdi(sedHendelse.getRinaSakId()).build(),
                        Tilleggsopplysning.builder().nokkel("rinaDokumentId").verdi(sedHendelse.getRinaDokumentId()).build()
                ))
                .build();
    }

    private static Bruker lagBruker(final String personIdent) {
        return Bruker.builder()
                .id(personIdent)
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

    private static List<Dokument> dokumenter(final String sedType,
                                             final SedMedVedlegg sedMedVedlegg,
                                             final DokkatSedInfo dokkatSedInfo) {
        final List<Dokument> dokumenter = new ArrayList<>();

        dokumenter.add(dokument(sedType, dokkatSedInfo.getDokumentTittel(), JournalpostFiltype.PDFA, sedMedVedlegg.getSed().getInnhold()));
        dokumenter.addAll(vedlegg(sedType, sedMedVedlegg.getVedleggListe()));
        return dokumenter;
    }

    private static Dokument dokument(final String sedType,
                                     final String filnavn,
                                     final JournalpostFiltype journalpostFiltype,
                                     final byte[] innhold) {
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

    private static List<Dokument> vedlegg(final String sedType,
                                          final List<SedMedVedlegg.BinaerFil> vedleggListe) {
        return vedleggListe.stream()
                .filter(gyldigFiltypePredicate)
                .map(binærfil ->
                        dokument(sedType,
                                isEmpty(binærfil.getFilnavn()) ? "Vedlegg" : binærfil.getFilnavn(),
                                JournalpostFiltype.fraMimeOgFilnavn(binærfil.getMimeType(), binærfil.getFilnavn())
                                        .orElseThrow(() -> new MappingException("Filtype kreves for "
                                                + binærfil.getFilnavn() + " (" + binærfil.getMimeType() + ")")),
                                binærfil.getInnhold())
                )
                .collect(Collectors.toList());
    }

    private static String temaForSedTypeOgJournalpostType(final String sedType,
                                                          final JournalpostType journalpostType) {
        // Hvis vi sender ut og ikke har en sak tilknyttet går man ut fra at det er medlemskap
        if (journalpostType == JournalpostType.UTGAAENDE) {
            return "MED";
        }

        return temaForSedType(sedType);
    }

    private static final Predicate<SedMedVedlegg.BinaerFil> gyldigFiltypePredicate = binaerFil -> {
        final boolean gyldigFiltype = JournalpostFiltype.fraMimeOgFilnavn(binaerFil.getMimeType(), binaerFil.getFilnavn())
                .map(JournalpostFiltype::erGyldigFiltypeForVariantformatArkiv)
                .orElse(Boolean.FALSE);

        if (!gyldigFiltype) {
            log.error("Et vedlegg av en SED har filtype som ikke støttes. "
                    + "Dette vedlegget kan ikke journalføres. Filnavn: {}", binaerFil.getFilnavn());
        }
        return gyldigFiltype;
    };
}
