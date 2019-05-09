package no.nav.melosys.eessi.models;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity(name = "fagsak_kobling")
public class FagsakKobling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gsak_saksnummer")
    private Long gsakSaksnummer;

    @OneToMany(mappedBy = "fagsakKobling", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<RinasakKobling> rinasakKoblinger = new ArrayList<>();

}
