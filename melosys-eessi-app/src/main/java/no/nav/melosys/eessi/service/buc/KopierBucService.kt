package no.nav.melosys.eessi.service.buc;

import java.util.Collections;
import java.util.Comparator;
import java.util.NoSuchElementException;

import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.buc.Document;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.springframework.stereotype.Service;

import static java.util.Optional.ofNullable;

@Service
public class KopierBucService {

    private static final int MAKS_LENGDE_YTTERLIGERE_INFO = 498; //500 minus to "\n"

    private final EuxService euxService;
    private final SaksrelasjonService saksrelasjonService;

    public KopierBucService(EuxService euxService, SaksrelasjonService saksrelasjonService) {
        this.euxService = euxService;
        this.saksrelasjonService = saksrelasjonService;
    }

    public String kopierBUC(String rinaSaksnummer) {
        var buc = euxService.hentBuc(rinaSaksnummer);
        var bucType = BucType.valueOf(buc.getBucType());
        var førsteSEDType = bucType.hentFørsteLovligeSed();

        var sed = buc.getDocuments().stream()
            .filter(d -> førsteSEDType.name().equals(d.getType()))
            .filter(Document::erOpprettet)
            .min(Comparator.comparing(Document::getCreationDate))
            .map(d -> euxService.hentSed(rinaSaksnummer, d.getId()))
            .orElseThrow(() -> new NoSuchElementException("Finner ikke første SED for rinasak " + rinaSaksnummer));

        settYtterligereInfo(sed, buc.getInternationalId());
        var nyttRinaSaksnummer = euxService.opprettBucOgSed(bucType, buc.hentMottakere(), sed, Collections.emptySet()).getRinaSaksnummer();

        saksrelasjonService.finnVedRinaSaksnummer(rinaSaksnummer)
            .ifPresent(saksrelasjon -> saksrelasjonService.lagreKobling(saksrelasjon.getGsakSaksnummer(), nyttRinaSaksnummer, bucType));

        return nyttRinaSaksnummer;
    }

    private void settYtterligereInfo(SED sed, String internasjonalID) {
        final var infoTekst = hentInfoTekst(sed.getSedType(), internasjonalID);
        final var ytterligereInfo = ofNullable(sed.getNav().getYtterligereinformasjon()).orElse("");

        if ((ytterligereInfo.length() + infoTekst.length()) > MAKS_LENGDE_YTTERLIGERE_INFO) {
            sed.getNav().setYtterligereinformasjon(infoTekst);
        } else {
            sed.getNav().setYtterligereinformasjon(ytterligereInfo + "\n\n" + infoTekst);
        }
    }

    private String hentInfoTekst(String sedType, String internasjonalID) {
        return String.format("""
            Due to an error in Rina, we are sending you a new %s.
            This BUC replaces a previously sent BUC with International ID: %s.
            We are unable to read your reply to our %s in the original BUC.
            Please reply in this BUC. We apologize for any inconvenience this may have caused.
            """, sedType, internasjonalID, sedType);
    }
}
