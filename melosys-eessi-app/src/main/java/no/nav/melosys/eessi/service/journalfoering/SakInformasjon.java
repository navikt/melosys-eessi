package no.nav.melosys.eessi.service.journalfoering;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SakInformasjon {
    private String journalpostId;
    private String dokumentId;
    private String gsakSaksnummer;
}
