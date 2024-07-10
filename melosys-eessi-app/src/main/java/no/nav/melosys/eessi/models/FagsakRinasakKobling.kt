package no.nav.melosys.eessi.models

import jakarta.persistence.*

@Entity(name = "FAGSAK_RINASAK_KOBLING")
class FagsakRinasakKobling(
    @Id
    @Column(name = "rina_saksnummer", nullable = false)
    var rinaSaksnummer: String,

    @Column(name = "gsak_saksnummer", nullable = false)
    var gsakSaksnummer: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "buc_type", nullable = false)
    var bucType: BucType
)
