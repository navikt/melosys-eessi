package no.nav.melosys.eessi.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaksrelasjonDto {
    private Long gsakSaksnummer;
    private String rinaSaksnummer;
    private String bucType;

    public static SaksrelasjonDto av(FagsakRinasakKobling fagsakRinasakKobling) {
        return new SaksrelasjonDto(
                fagsakRinasakKobling.getGsakSaksnummer(),
                fagsakRinasakKobling.getRinaSaksnummer(),
                fagsakRinasakKobling.getBucType().name()
        );
    }
}
