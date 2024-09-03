package no.nav.melosys.eessi.integration.pdl.dto;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

public interface HarMetadata {
    PDLMetadata getMetadata();

    static <T extends HarMetadata> Optional<T> hentSisteOpplysning(Collection<T> opplysning) {
        return opplysning.stream()
            .max(Comparator.comparing(harMetadata -> harMetadata.getMetadata().sisteDatoOpprettetEllerKorrigert()));
    }
}
