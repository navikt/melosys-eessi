package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag

import no.nav.melosys.eessi.controller.dto.Adressetype
import no.nav.melosys.eessi.controller.dto.Ident
import no.nav.melosys.eessi.controller.dto.SedGrunnlagDto
import no.nav.melosys.eessi.controller.dto.Virksomhet
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.nav.*
import no.nav.melosys.eessi.service.sed.helpers.StreamUtils
import java.util.*
import java.util.stream.Collectors

interface SedGrunnlagMapper {
    fun map(sed: SED): SedGrunnlagDto {
        val nav = sed.nav
        val sedGrunnlagDto = SedGrunnlagDto()

        sedGrunnlagDto.bostedsadresse = mapBosted(nav!!.bruker!!.adresse)
        sedGrunnlagDto.utenlandskIdent = mapUtenlandskIdent(nav.bruker!!.person!!.pin)
        sedGrunnlagDto.arbeidssteder = mapArbeidssteder(nav.arbeidssted)
        sedGrunnlagDto.arbeidsland = mapArbeidsland(nav.arbeidsland)
        sedGrunnlagDto.arbeidsgivendeVirksomheter = mapVirksomheter(nav.arbeidsgiver)
        sedGrunnlagDto.selvstendigeVirksomheter = mapSelvstendig(nav.selvstendig)
        sedGrunnlagDto.ytterligereInformasjon = nav.ytterligereinformasjon

        return sedGrunnlagDto
    }

    fun mapBosted(adresser: List<Adresse>?): no.nav.melosys.eessi.controller.dto.Adresse {
        return StreamUtils.nullableStream(adresser)
            .filter { adresse: Adresse -> erBostedsadresse(adresse) }.findFirst()
            .map { adresseFraRina: Adresse? ->
                no.nav.melosys.eessi.controller.dto.Adresse.av(
                    adresseFraRina
                )
            }
            .orElse(mapAdresse(adresser))
    }

    fun mapAdresse(adresser: List<Adresse>?): no.nav.melosys.eessi.controller.dto.Adresse {
        return StreamUtils.nullableStream(adresser).findFirst()
            .map { adresseFraRina: Adresse? ->
                no.nav.melosys.eessi.controller.dto.Adresse.av(
                    adresseFraRina
                )
            }.orElseGet { no.nav.melosys.eessi.controller.dto.Adresse() }
    }

    fun mapUtenlandskIdent(pins: Collection<Pin>?): List<Ident> {
        return StreamUtils.nullableStream(pins).map { pin: Pin? -> Ident.av(pin) }.filter { obj: Ident -> obj.erUtenlandsk() }
            .collect(Collectors.toList())
    }

    fun mapArbeidssteder(arbeidssted: List<Arbeidssted>?): List<no.nav.melosys.eessi.controller.dto.Arbeidssted> {
        return StreamUtils.nullableStream(arbeidssted)
            .map { arbeidsstedFraRina: Arbeidssted? -> no.nav.melosys.eessi.controller.dto.Arbeidssted.av(arbeidsstedFraRina) }
            .collect(Collectors.toList())
    }

    fun mapArbeidsland(arbeidsland: List<Arbeidsland>?): List<no.nav.melosys.eessi.controller.dto.Arbeidsland> {
        return StreamUtils.nullableStream(arbeidsland)
            .map { arbeidslandFraRina: Arbeidsland? -> no.nav.melosys.eessi.controller.dto.Arbeidsland.av(arbeidslandFraRina) }
            .collect(Collectors.toList())
    }


    fun mapVirksomheter(arbeidsgivere: List<Arbeidsgiver>?): List<Virksomhet> {
        return StreamUtils.nullableStream(arbeidsgivere).map { arbeidsgiver: Arbeidsgiver? -> Virksomhet.av(arbeidsgiver) }
            .collect(Collectors.toList())
    }

    fun mapSelvstendig(selvstendig: Selvstendig?): List<Virksomhet> {
        return Optional.ofNullable(selvstendig).stream()
            .map<List<Arbeidsgiver?>>(Selvstendig::arbeidsgiver)
            .flatMap { obj: List<Arbeidsgiver?> -> obj.stream() }
            .map { arbeidsgiver: Arbeidsgiver? -> Virksomhet.av(arbeidsgiver) }
            .collect(Collectors.toList())
    }

    companion object {
        fun erBostedsadresse(adresse: Adresse): Boolean {
            return Adressetype.BOSTEDSADRESSE.adressetypeRina.equals(adresse.type, ignoreCase = true)
        }
    }
}
