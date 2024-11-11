package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag

import no.nav.melosys.eessi.controller.dto.Adressetype
import no.nav.melosys.eessi.controller.dto.Ident
import no.nav.melosys.eessi.controller.dto.SedGrunnlagDto
import no.nav.melosys.eessi.controller.dto.Virksomhet
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.nav.*

interface SedGrunnlagMapper {
    fun map(sed: SED): SedGrunnlagDto {
        val nav = sed.nav ?: throw NullPointerException("sed.nav kan ikke være null")
        val bruker = nav.bruker ?: throw NullPointerException("sed.nav.bruker kan ikke være null")
        val person = bruker.person ?: throw NullPointerException("sed.nav.bruker.person kan ikke være null")
        return SedGrunnlagDto(
            bostedsadresse = mapBosted(bruker.adresse),
            utenlandskIdent = mapUtenlandskIdent(person.pin),
            arbeidssteder = mapArbeidssteder(nav.arbeidssted),
            arbeidsland = mapArbeidsland(nav.arbeidsland),
            arbeidsgivendeVirksomheter = mapVirksomheter(nav.arbeidsgiver),
            selvstendigeVirksomheter = mapSelvstendig(nav.selvstendig),
            ytterligereInformasjon = nav.ytterligereinformasjon
        )
    }

    fun mapBosted(adresser: List<Adresse>?): no.nav.melosys.eessi.controller.dto.Adresse = adresser?.firstOrNull { erBostedsadresse(it) }
        ?.let { no.nav.melosys.eessi.controller.dto.Adresse.av(it) }
        ?: mapAdresse(adresser)

    fun mapAdresse(adresser: List<Adresse>?): no.nav.melosys.eessi.controller.dto.Adresse = adresser?.firstOrNull()
        ?.let { no.nav.melosys.eessi.controller.dto.Adresse.av(it) }
        ?: no.nav.melosys.eessi.controller.dto.Adresse()

    fun mapUtenlandskIdent(pins: Collection<Pin>?): List<Ident> =
        pins?.mapNotNull { pin -> Ident.av(pin).takeIf { it.erUtenlandsk() } } ?: emptyList()

    fun mapArbeidssteder(arbeidssted: List<Arbeidssted>?): List<no.nav.melosys.eessi.controller.dto.Arbeidssted> =
        arbeidssted?.map { no.nav.melosys.eessi.controller.dto.Arbeidssted.av(it) } ?: emptyList()

    fun mapArbeidsland(arbeidsland: List<Arbeidsland>?): List<no.nav.melosys.eessi.controller.dto.Arbeidsland> =
        arbeidsland?.map { no.nav.melosys.eessi.controller.dto.Arbeidsland.av(it) } ?: emptyList()

    fun mapVirksomheter(arbeidsgivere: List<Arbeidsgiver>?): List<Virksomhet> = arbeidsgivere?.map { Virksomhet.av(it) } ?: emptyList()

    fun mapSelvstendig(selvstendig: Selvstendig?): List<Virksomhet> = selvstendig?.arbeidsgiver?.map { Virksomhet.av(it) } ?: emptyList()

    fun erBostedsadresse(adresse: Adresse): Boolean = Adressetype.BOSTEDSADRESSE.adressetypeRina.equals(adresse.type, ignoreCase = true)
}
