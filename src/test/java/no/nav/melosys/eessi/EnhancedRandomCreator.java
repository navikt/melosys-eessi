package no.nav.melosys.eessi;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;

public class EnhancedRandomCreator {

    public static EnhancedRandom defaultEnhancedRandom() {
        return EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
                .collectionSizeRange(1, 4)
                .overrideDefaultInitialization(true)
                .build();
    }
}
