package no.nav.melosys.eessi.integration.pdl;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

import no.nav.melosys.eessi.integration.pdl.dto.HarMetadata;

final class PDLUtils {

    private PDLUtils() {}

    static <T extends HarMetadata> Optional<T> hentSisteOpplysning(Collection<T> opplysning) {
        return opplysning.stream().max(sistEndretComparator);
    }

    private static final Comparator<HarMetadata> sistEndretComparator = Comparator.comparing(harMetadata -> harMetadata.getMetadata().sisteDatoOpprettetEllerKorrigert());
}
