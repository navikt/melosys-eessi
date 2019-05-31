package no.nav.melosys.eessi.service.sed;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import no.nav.melosys.eessi.controller.dto.BucSedRelasjonDto;
import no.nav.melosys.eessi.models.BucType;
import org.springframework.stereotype.Service;

@Service
public class BucService {

    public BucService() {
    }

    public List<BucSedRelasjonDto> hentBucSedRelasjoner() {
        return Arrays.stream(BucType.values())
                .map(bucType -> BucSedRelasjonDto.builder()
                        .buc(bucType.name())
                        .forsteSed(SedUtils.hentFoersteLovligeSedPaaBuc(bucType).name())
                        .fagomrade(SedUtils.hentFagomraadeForBuc(bucType).name())
                        .build())
                .collect(Collectors.toList());
    }
}
