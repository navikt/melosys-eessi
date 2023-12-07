package no.nav.melosys.eessi.integration.journalpostapi;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
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
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static no.nav.melosys.eessi.integration.journalpostapi.JournalpostFiltype.*;
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
                                                                         final Boolean skalKonvertereTilPDF) {
        return opprettJournalpostRequest(JournalpostType.INNGAAENDE, sedHendelse, sedMedVedlegg, sak, dokumentTittel, behandlingstema, personIdent, skalKonvertereTilPDF);
    }

    public static OpprettJournalpostRequest opprettUtgaaendeJournalpost(final SedHendelse sedHendelse,
                                                                        final SedMedVedlegg sedMedVedlegg,
                                                                        final Sak sak,
                                                                        final String dokumentTittel,
                                                                        final String behandlingstema,
                                                                        final String personIdent,
                                                                        final Boolean skalKonvertereTilPDF) {
        return opprettJournalpostRequest(JournalpostType.UTGAAENDE, sedHendelse, sedMedVedlegg, sak, dokumentTittel, behandlingstema, personIdent, skalKonvertereTilPDF);
    }


    private static OpprettJournalpostRequest opprettJournalpostRequest(final JournalpostType journalpostType,
                                                                       final SedHendelse sedHendelse,
                                                                       final SedMedVedlegg sedMedVedlegg,
                                                                       final Sak sak,
                                                                       final String dokumentTittel,
                                                                       final String behandlingstema,
                                                                       final String personIdent,
                                                                       final Boolean skalKonvertereTilPDF) {

        return OpprettJournalpostRequest.builder()
            .avsenderMottaker(getAvsenderMottaker(journalpostType, sedHendelse))
            .behandlingstema(behandlingstema)
            .bruker(isNotEmpty(personIdent) ? lagBruker(personIdent) : null)
            .dokumenter(dokumenter(sedHendelse.getSedType(), sedMedVedlegg, dokumentTittel, skalKonvertereTilPDF))
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

    private static List<Dokument> dokumenter(final String sedType,
                                             final SedMedVedlegg sedMedVedlegg,
                                             final String dokumentTittel,
                                             final Boolean skalKonvertereTilPDF) {
        final List<Dokument> dokumenter = new ArrayList<>();

        dokumenter.add(dokument(sedType, dokumentTittel, JournalpostFiltype.PDFA, sedMedVedlegg.getSed().getInnhold()));
        dokumenter.addAll(vedlegg(sedType, sedMedVedlegg.getVedleggListe(), skalKonvertereTilPDF));
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
                                          final List<SedMedVedlegg.BinaerFil> vedleggListe,
                                          final Boolean skalKonvertereTilPDF) {

        log.info("KonverteringPDF: toggle er {}", skalKonvertereTilPDF);
        return vedleggListe.stream()
            .map(binærfil -> {
                JournalpostFiltype opprinneligFiltype = JournalpostFiltype.fraMimeOgFilnavn(binærfil.getMimeType(), binærfil.getFilnavn(), skalKonvertereTilPDF).orElseThrow(() -> new MappingException("Filtype kreves for "
                    + binærfil.getFilnavn() + " (" + binærfil.getMimeType() + ")"));

                return dokument(sedType,
                    isEmpty(binærfil.getFilnavn()) ? "Vedlegg" : binærfil.getFilnavn(),
                    skalKonvertereTilPDF ? PDF : opprinneligFiltype,
                    skalKonvertereTilPDF ? getPdfByteArray(binærfil, opprinneligFiltype) : binærfil.getInnhold());
                }
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
        final boolean gyldigFiltype = JournalpostFiltype.fraMimeOgFilnavn(binaerFil.getMimeType(), binaerFil.getFilnavn(), false)
            .map(JournalpostFiltype::erGyldigFiltypeForVariantformatArkiv)
            .orElse(Boolean.FALSE);

        if (!gyldigFiltype) {
            log.error("Et vedlegg av en SED har filtype som ikke støttes. "
                + "Dette vedlegget kan ikke journalføres. Filnavn: {}", binaerFil.getFilnavn());
        }
        return gyldigFiltype;
    };

    private static byte[] getPdfByteArray(SedMedVedlegg.BinaerFil binaerFil, JournalpostFiltype filtype) {
        log.info("KonverteringPDF: Konverter fra {} til PDF", filtype);
        switch (filtype) {
            case PDF: {
                return binaerFil.getInnhold();
            }
            case DOCX: {
                return convertWordToPdf(binaerFil, filtype).toByteArray();
            }
            case TIFF:
            case JPEG: {
                return convertImageToPdf(binaerFil, filtype).toByteArray();
            }
            default:
                throw new RuntimeException("Mangler logikk for vedleggstype " + filtype);
        }
    }

//    private static ByteArrayOutputStream convertWordToPdf(SedMedVedlegg.BinaerFil binaerFil, JournalpostFiltype konverterbarFiltype) {
//        InputStream is = new ByteArrayInputStream(binaerFil.getInnhold());
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//        try (
//            XWPFDocument document = new XWPFDocument(is)
//        ) {
//
//            if (konverterbarFiltype == JournalpostFiltype.DOCX) {
//                Document pdfDocument = new Document();
//                PdfWriter.getInstance(pdfDocument, out);
//                pdfDocument.open();
//
//
//                List<XWPFParagraph> paragraphs = document.getParagraphs();
//                for (XWPFParagraph paragraph : paragraphs) {
//                    pdfDocument.add(new Paragraph(paragraph.getText()));
//                }
//                pdfDocument.close();
//            } else {
//                throw new IllegalArgumentException("Ikke implementert konvertering for filtype: " + konverterbarFiltype);
//            }
//        } catch (IOException | DocumentException | StackOverflowError e) {
//            throw new RuntimeException("KonverteringPDF: Kunne ikke konvertere");
//        }
//        return out;
//    }

    protected static ByteArrayOutputStream convertWordToPdf(SedMedVedlegg.BinaerFil binaerFil, JournalpostFiltype konverterbarFiltype) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            InputStream is = new ByteArrayInputStream(binaerFil.getInnhold());

            if (konverterbarFiltype == JournalpostFiltype.DOCX) {
                ZipSecureFile.setMinInflateRatio(MIN_INFLATE_RATIO);
                XWPFDocument document = new XWPFDocument(is);
                PdfOptions options = PdfOptions.create();
                PdfConverter.getInstance().convert(document, out, options);
            } else {
                throw new IllegalArgumentException("Ikke implementert støtte for konvertering av filtype " + konverterbarFiltype);
            }
        } catch (IOException | StackOverflowError e) { // StackOverflowError kan kastes av PDF-konverteringen, f.eks. ved uendelig forsøk på tekstbryting
            throw new RuntimeException("Kunne ikke konvertere vedlegg " + binaerFil.getFilnavn() +
                " med MIME-type " + binaerFil.getMimeType() + "  til PDF", e);
        }
        return out;
    }



    private static ByteArrayOutputStream convertImageToPdf(SedMedVedlegg.BinaerFil binaerFil, JournalpostFiltype filtype) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PDDocument doc = new PDDocument()) {
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
        } catch (IOException e) {
            throw new RuntimeException("KonverteringPDF: Kunne ikke konvertere vedlegg " + binaerFil.getFilnavn() +
                " med MIME-type " + binaerFil.getMimeType() + "  til PDF", e);
        }
        return baos;
    }

}
