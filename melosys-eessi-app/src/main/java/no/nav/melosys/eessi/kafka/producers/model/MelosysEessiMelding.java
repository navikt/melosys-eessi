package no.nav.melosys.eessi.kafka.producers.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class MelosysEessiMelding {

    private String sedId;
    private String sedVersjon;
    private UUID batchID;
    private String rinaSaksnummer;
    private Avsender avsender;
    private String journalpostId;
    private String dokumentId;
    private Long gsakSaksnummer;
    private String aktoerId;
    private List<Statsborgerskap> statsborgerskap = new ArrayList<>();
    private List<Arbeidssted> arbeidssteder = new ArrayList<>();
    private Periode periode;
    private String lovvalgsland;
    private String artikkel;
    private boolean erEndring;
    private boolean midlertidigBestemmelse;
    private boolean x006NavErFjernet;
    private String ytterligereInformasjon;
    private String bucType;
    private String sedType;
    private SvarAnmodningUnntak svarAnmodningUnntak;
    private AnmodningUnntak anmodningUnntak;
}
