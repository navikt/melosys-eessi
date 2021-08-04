package no.nav.melosys.eessi.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.melosys.eessi.models.buc.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SedinfoDto {

    private String bucId;
    private String sedId;
    private Long opprettetDato;
    private Long sistOppdatert;
    private String sedType;
    private String status;
    private String rinaUrl;

    public static SedinfoDto av(Document document, String bucId, String rinaSedUrl) {
        return SedinfoDto.builder()
                .bucId(bucId)
                .sedId(document.getId())
                .sedType(document.getType())
                .opprettetDato(document.getCreationDate().toInstant().toEpochMilli())
                .sistOppdatert(document.getLastUpdate().toInstant().toEpochMilli())
                .status(tilNorskStatusEllerTomString(document.getStatus()))
                .rinaUrl(rinaSedUrl)
                .build();
    }

    private static String tilNorskStatusEllerTomString(String status) {
        SedStatus sedStatus = SedStatus.fraEngelskStatus(status);
        if (sedStatus == null) {
            return "";
        }

        return sedStatus.getNorskStatus();
    }
}
