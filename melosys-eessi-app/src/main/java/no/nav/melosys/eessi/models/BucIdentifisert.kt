package no.nav.melosys.eessi.models

import jakarta.persistence.*

@Entity(name = "buc_identifisert")
class BucIdentifisert(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0,

    @Column(name = "rina_saksnummer", updatable = false, unique = true)
    val rinaSaksnummer: String,

    @Column(name = "folkeregisterident", updatable = false)
    val folkeregisterident: String
)
