package no.nav.melosys.eessi.controller.dto;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Document;
import org.springframework.util.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BucinfoDto {

    private String id;
    private String bucType;
    private Long opprettetDato;
    private List<SedinfoDto> seder;

    public static BucinfoDto av(BUC buc, String status, String rinaUrlPrefix) {
        return BucinfoDto.builder()
                .id(buc.getId())
                .bucType(buc.getBucType())
                .opprettetDato(buc.getStartDate().toInstant().toEpochMilli())
                .seder(buc.getDocuments().stream()
                        .filter(filtrerMedStatus(status))
                        .map(doc -> SedinfoDto.av(doc, buc.getId(), rinaUrlPrefix))
                        .collect(Collectors.toList()))
                .build();
    }

    private static Predicate<Document> filtrerMedStatus(String status) {
        if (StringUtils.isEmpty(status)) {
            return b -> true;
        }

        return b -> SedStatus.fraEngelskStatus(b.getStatus()) == SedStatus.fraNorskStatus(status);
    }
}
