package no.nav.melosys.eessi.kafka.producers;

import java.util.List;
import lombok.Data;

@Data
public class MelosysEessiMelding {

    private String sedId;
    private String rinaSaksnummer;
    private String journalpostId;
    private String dokumentId;
    private Long gsakSaksnummer;
    private String aktoerId;
    private List<Statsborgerskap> statsborgerskap;
    private Periode periode;
    private String lovvalgsland;
    private String artikkel;
    private boolean erEndring;
    private boolean midlertidigBestemmelse;
    private String ytterligereInformasjon;
    private String bucType;
    private String sedType;

    private SvarAnmodningUnntak svarAnmodningUnntak;
}
