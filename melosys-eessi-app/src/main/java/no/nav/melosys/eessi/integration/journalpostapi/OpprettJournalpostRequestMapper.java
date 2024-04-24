package no.nav.melosys.eessi.integration.journalpostapi;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fr.opensagres.poi.xwpf.converter.core.XWPFConverterException;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.metrikker.SedMetrikker;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import static no.nav.melosys.eessi.integration.journalpostapi.JournalpostFiltype.PDF;
import static no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostRequest.*;
import static no.nav.melosys.eessi.service.sed.SedTypeTilTemaMapper.temaForSedType;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
public final class OpprettJournalpostRequestMapper {
    public static final double MIN_INFLATE_RATIO = 0.001;
    public static final float PDF_MARGIN = 20.0f;

    public static OpprettJournalpostRequest opprettInngaaendeJournalpost(final SedHendelse sedHendelse,
                                                                         final SedMedVedlegg sedMedVedlegg,
                                                                         final Sak sak,
                                                                         final String dokumentTittel,
                                                                         final String behandlingstema,
                                                                         final String personIdent,
                                                                         final SedMetrikker sedMetrikker) {
        return opprettJournalpostRequest(JournalpostType.INNGAAENDE, sedHendelse, sedMedVedlegg, sak, dokumentTittel,
            behandlingstema, personIdent, sedMetrikker);
    }

    public static OpprettJournalpostRequest opprettUtgaaendeJournalpost(final SedHendelse sedHendelse,
                                                                        final SedMedVedlegg sedMedVedlegg,
                                                                        final Sak sak,
                                                                        final String dokumentTittel,
                                                                        final String behandlingstema,
                                                                        final String personIdent,
                                                                        final SedMetrikker sedMetrikker) {
        return opprettJournalpostRequest(JournalpostType.UTGAAENDE, sedHendelse, sedMedVedlegg, sak, dokumentTittel,
            behandlingstema, personIdent, sedMetrikker);
    }


    private static OpprettJournalpostRequest opprettJournalpostRequest(final JournalpostType journalpostType,
                                                                       final SedHendelse sedHendelse,
                                                                       final SedMedVedlegg sedMedVedlegg,
                                                                       final Sak sak,
                                                                       final String dokumentTittel,
                                                                       final String behandlingstema,
                                                                       final String personIdent,
                                                                       final SedMetrikker sedMetrikker) {

        return OpprettJournalpostRequest.builder()
            .avsenderMottaker(getAvsenderMottaker(journalpostType, sedHendelse))
            .behandlingstema(behandlingstema)
            .bruker(isNotEmpty(personIdent) ? lagBruker(personIdent) : null)
            .dokumenter(dokumenter(sedHendelse, sedMedVedlegg, dokumentTittel, sedMetrikker))
            .eksternReferanseId(sedHendelse.getSedId())
            .journalfoerendeEnhet("4530")
            .journalpostType(journalpostType)
            .kanal("EESSI")
            .sak(sak != null ? OpprettJournalpostRequest.Sak.builder().arkivsaksnummer(sak.getId()).build() : null)
            .tema(sak != null ? sak.getTema() : temaForSedTypeOgJournalpostType(sedHendelse.getSedType(), journalpostType))
            .tittel(dokumentTittel)
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

    private static List<Dokument> dokumenter(final SedHendelse sedHendelse,
                                             final SedMedVedlegg sedMedVedlegg,
                                             final String dokumentTittel,
                                             final SedMetrikker sedMetrikker) {
        final List<Dokument> dokumenter = new ArrayList<>();

        dokumenter.add(dokument(sedHendelse.getSedType(), dokumentTittel, JournalpostFiltype.PDFA,
            sedMedVedlegg.getSed().getInnhold()));
        dokumenter.addAll(vedlegg(sedHendelse, sedMedVedlegg.getVedlegg(), sedMetrikker));
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

    private static List<Dokument> vedlegg(final SedHendelse sedHendelse,
                                          final List<SedMedVedlegg.BinaerFil> vedleggListe, SedMetrikker sedMetrikker) {
        List<Dokument> vedlegg = new ArrayList<>();
        for (SedMedVedlegg.BinaerFil binaerFil : vedleggListe) {
            try {
                JournalpostFiltype opprinneligFiltype =
                    JournalpostFiltype.fraMimeOgFilnavn(binaerFil.getMimeType(), binaerFil.getFilnavn()).orElseThrow(MappingException::new);
                vedlegg.add(
                    dokument(
                        sedHendelse.getSedType(),
                        isEmpty(binaerFil.getFilnavn()) ? "Vedlegg" : binaerFil.getFilnavn(),
                        PDF,
                        getPdfByteArray(binaerFil, opprinneligFiltype)
                    )
                );
            } catch (XWPFConverterException | IOException | StackOverflowError | MappingException e) {
                log.error("Kunne ikke konvertere vedlegg %s med MIME-type %s til PDF. RINA saksnummer: %s"
                    .formatted(binaerFil.getFilnavn(), binaerFil.getMimeType(), sedHendelse.getRinaSakId()));
                sedMetrikker.sedPdfKonverteringFeilet();
            }
        }

        return vedlegg;
    }

    private static String temaForSedTypeOgJournalpostType(final String sedType,
                                                          final JournalpostType journalpostType) {
        // Hvis vi sender ut og ikke har en sak tilknyttet går man ut fra at det er medlemskap
        if (journalpostType == JournalpostType.UTGAAENDE) {
            return "MED";
        }

        return temaForSedType(sedType);
    }

    private static byte[] getPdfByteArray(SedMedVedlegg.BinaerFil binaerFil, JournalpostFiltype filtype) throws IOException {
        if (filtype != PDF) log.info("Konverter fra {} til PDF", filtype);
        switch (filtype) {
            case PDF: {
                return binaerFil.getInnhold();
            }
            case DOCX: {
                return konverterWordTilPdf(binaerFil).toByteArray();
            }
            case TIFF:
            case JPEG: {
                return konverterBildeTilPdf(binaerFil, filtype).toByteArray();
            }
            default:
                return binaerFil.getInnhold();
        }
    }

    private static ByteArrayOutputStream konverterWordTilPdf(SedMedVedlegg.BinaerFil binaerFil) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        InputStream is = new ByteArrayInputStream(binaerFil.getInnhold());
        ZipSecureFile.setMinInflateRatio(MIN_INFLATE_RATIO);
        XWPFDocument document = new XWPFDocument(is);
        PdfOptions options = PdfOptions.create();
        PdfConverter.getInstance().convert(document, out, options);

        return out;
    }

    private static ByteArrayOutputStream konverterBildeTilPdf(SedMedVedlegg.BinaerFil binaerFil, JournalpostFiltype filtype) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PDDocument doc = new PDDocument();
        InputStream in = new ByteArrayInputStream(binaerFil.getInnhold());
        BufferedImage bImageFromConvert = ImageIO.read(in);
        PDImageXObject pdImage;
        if (filtype == JournalpostFiltype.JPEG) {
            pdImage = JPEGFactory.createFromImage(doc, bImageFromConvert);
        } else {
            pdImage = LosslessFactory.createFromImage(doc, bImageFromConvert);
        }

        PDRectangle rectangle = new PDRectangle(pdImage.getWidth() + PDF_MARGIN * 2, pdImage.getHeight() + PDF_MARGIN * 2);
        PDPage page = new PDPage(rectangle);
        doc.addPage(page);

        try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
            contents.drawImage(pdImage, PDF_MARGIN, PDF_MARGIN);
        }
        doc.save(baos);
        return baos;
    }

}
