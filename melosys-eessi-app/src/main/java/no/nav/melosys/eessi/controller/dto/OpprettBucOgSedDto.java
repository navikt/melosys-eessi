package no.nav.melosys.eessi.controller.dto;

import java.util.Collection;
import java.util.Collections;

import lombok.Data;
import no.nav.melosys.eessi.models.SedVedlegg;

@Data
public class OpprettBucOgSedDto {
    private SedDataDto sedDataDto;
    private Collection<SedVedlegg> vedlegg = Collections.emptySet();
}
