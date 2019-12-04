package no.nav.melosys.eessi.kafka.producers.model;

import java.util.ArrayList;
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
    private List<Statsborgerskap> statsborgerskap = new ArrayList<>();
    private Periode periode;
    private String lovvalgsland;
    private String artikkel;
    private boolean erEndring;
    private boolean midlertidigBestemmelse;
    private String ytterligereInformasjon;
    private String bucType;
    private String sedType;

    private SvarAnmodningUnntak svarAnmodningUnntak;
    private AnmodningUnntak anmodningUnntak;
}
