package no.nav.melosys.eessi.service.sed.mapper.til_sed

import mu.KotlinLogging
import no.nav.melosys.eessi.controller.dto.*
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.exception.MappingException
import no.nav.melosys.eessi.models.sed.*
import no.nav.melosys.eessi.models.sed.nav.*
import no.nav.melosys.eessi.models.sed.nav.Adresse
import no.nav.melosys.eessi.models.sed.nav.Arbeidsland
import no.nav.melosys.eessi.models.sed.nav.Arbeidssted
import no.nav.melosys.eessi.models.sed.nav.Bruker
import no.nav.melosys.eessi.models.sed.nav.Periode
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper
import org.springframework.util.StringUtils
import java.time.LocalDate

private val log = KotlinLogging.logger { }

/**
 * Felles mapper-interface for alle typer SED. Mapper NAV-objektet i NAV-SED, som brukes av eux for
 * å plukke ut nødvendig informasjon for en angitt SED.
 */
interface SedMapper {

    fun getSedType(): SedType

    fun mapTilSed(sedData: SedDataDto, erCDM4_3: Boolean): SED {
        return SED(
            nav = prefillNav(sedData, erCDM4_3),
            sedType = getSedType().name,
            sedGVer = Konstanter.DEFAULT_SED_G_VER,
            sedVer = if (erCDM4_3) Konstanter.SED_VER_CDM_4_3 else Konstanter.DEFAULT_SED_VER
        )
    }

    fun prefillNav(sedData: SedDataDto, erCDM4_3: Boolean): Nav {
        val sedType = getSedType()

        val arbeidsland: List<Arbeidsland>? =
            if (erCDM4_3 && sedType in listOf(SedType.A001, SedType.A002, SedType.A003, SedType.A008, SedType.A009, SedType.A010)) {
                hentArbeidsland(sedData).takeIf { it.isNotEmpty() }
            } else {
                null
            }

        return Nav(
            arbeidssted = if (arbeidsland == null) hentArbeidssted(sedData) else null,
            arbeidsland = arbeidsland,
            bruker = hentBruker(sedData),
            arbeidsgiver = hentArbeidsgivereILand(sedData.arbeidsgivendeVirksomheter ?: emptyList(), sedData.finnLovvalgslandDefaultNO()),
            ytterligereinformasjon = sedData.ytterligereInformasjon,
            selvstendig = if (sedData.selvstendigeVirksomheter!!.isNotEmpty()) hentSelvstendig(sedData) else null
        )
    }

    fun hentBruker(sedDataDto: SedDataDto) = Bruker(
        person = hentPerson(sedDataDto),
        adresse = hentAdresser(sedDataDto)
    ).apply {
        setFamiliemedlemmer(sedDataDto, this)
    }

    fun hentPerson(sedData: SedDataDto): Person {
        val bruker = sedData.bruker ?: throw NullPointerException("sedData.bruker er null")
        return Person(
            fornavn = bruker.fornavn,
            etternavn = bruker.etternavn,
            foedselsdato = formaterDato(bruker.foedseldato ?: throw NullPointerException("bruker.foedseldato er null")),
            foedested = null, //det antas at ikke trengs når NAV fyller ut.
            kjoenn = Kjønn.valueOf(bruker.kjoenn ?: throw NullPointerException("bruker.kjoenn er null")),
            statsborgerskap = hentStatsborgerskap(sedData),
            pin = hentPin(sedData)
        )
    }

    fun hentStatsborgerskap(sedDataDto: SedDataDto): List<Statsborgerskap> {
        val statsborgerskapStringListe = sedDataDto.bruker?.statsborgerskap ?: emptyList()
        val statsborgerskapList = statsborgerskapStringListe
            .filter { LandkodeMapper.finnLandkodeIso2(it).isPresent }
            .map { lagStatsborgerskap(it) }

        statsborgerskapList.forEach {
            if (it.land == LandkodeMapper.KOSOVO_LANDKODE_ISO2) {
                it.land = LandkodeMapper.UKJENT_LANDKODE_ISO2
                log.info("Endrer statsborgerskap fra Kosovo til Ukjent. gsakSaksnummer: ${sedDataDto.gsakSaksnummer}")
            }
        }

        if (statsborgerskapList.isEmpty()) {
            throw MappingException("Statsborgerskap mangler eller er ugyldig. statsborgerskap fra sedData: ${statsborgerskapStringListe.joinToString(", ")}")
        }
        return statsborgerskapList
    }

    fun lagStatsborgerskap(landkode: String) = Statsborgerskap(LandkodeMapper.mapTilLandkodeIso2(landkode))

    fun hentPin(sedData: SedDataDto): List<Pin> {
        val pins = mutableListOf(
            Pin(sedData.bruker?.fnr ?: "", "NO", null)
        )

        sedData.utenlandskIdent?.forEach {
            pins.add(Pin(it.ident, LandkodeMapper.mapTilLandkodeIso2(it.landkode), null))
        }

        return pins
    }

    fun hentAdresser(sedDataDto: SedDataDto): List<Adresse> = listOfNotNull(
        sedDataDto.bostedsadresse?.let { mapBostedsadresse(it) },
        sedDataDto.kontaktadresse?.let { mapAdresse(it) },
        sedDataDto.oppholdsadresse?.let { mapAdresse(it) }
    )

    fun mapBostedsadresse(adresse: no.nav.melosys.eessi.controller.dto.Adresse): Adresse =
        mapAdresse(adresse).copy(
            type = if (adresse.adressetype == Adressetype.BOSTEDSADRESSE) Adressetype.BOSTEDSADRESSE.adressetypeRina else ""
        )

    fun mapAdresse(adresse: no.nav.melosys.eessi.controller.dto.Adresse) = Adresse(
        type = adresse.adressetype?.adressetypeRina ?: "",
        gate = adresse.gateadresse,
        by = adresse.poststed,
        postnummer = adresse.postnr,
        region = adresse.region,
        land = LandkodeMapper.mapTilLandkodeIso2(adresse.land)
    )

    fun setFamiliemedlemmer(sedData: SedDataDto, bruker: Bruker) {
        sedData.familieMedlem?.find { it.relasjon.equals("FAR", ignoreCase = true) }?.let {
            bruker.far = Far(person = Person(fornavn = it.fornavn, etternavnvedfoedsel = it.etternavn))
        }
        sedData.familieMedlem?.find { it.relasjon.equals("MOR", ignoreCase = true) }?.let {
            bruker.mor = Mor(person = Person(fornavn = it.fornavn, etternavnvedfoedsel = it.etternavn))
        }
    }

    fun hentArbeidsland(sedData: SedDataDto): List<Arbeidsland> = sedData.arbeidsland?.map {
        Arbeidsland(
            land = it.land,
            arbeidssted = hentArbeidssted4_3(it.arbeidssted)
        )
    } ?: emptyList()

    fun hentArbeidssted4_3(arbeidssteder: List<no.nav.melosys.eessi.controller.dto.Arbeidssted>): List<Arbeidssted> = arbeidssteder.map {
        Arbeidssted(
            navn = it.navn,
            adresse = hentAdresseFraDtoAdresse(it.adresse!!),
            hjemmebase = landkodeIso2EllerNull(it.hjemmebase),
            erikkefastadresse = if (it.fysisk) "nei" else "ja"
        )
    }

    fun hentArbeidssted(sedData: SedDataDto): List<Arbeidssted> = sedData.arbeidssteder?.map {
        Arbeidssted(
            navn = it.navn,
            adresse = hentAdresseFraDtoAdresse(it.adresse!!),
            hjemmebase = landkodeIso2EllerNull(it.hjemmebase),
            erikkefastadresse = if (it.fysisk) "nei" else "ja"
        )
    } ?: emptyList()

    fun hentArbeidsgivereILand(virksomheter: List<Virksomhet>, landkode: String): List<Arbeidsgiver> =
        hentArbeidsgiver(virksomheter) { it.adresse!!.land == landkode }

    fun hentArbeidsgivereIkkeILand(virksomheter: List<Virksomhet>, landkode: String): List<Arbeidsgiver> =
        hentArbeidsgiver(virksomheter) { it.adresse!!.land != landkode }

    fun hentArbeidsgiver(virksomheter: List<Virksomhet>, predicate: (Virksomhet) -> Boolean): List<Arbeidsgiver> =
        virksomheter.filter(predicate).map { hentArbeidsgiver(it) }

    fun hentArbeidsgiver(virksomhet: Virksomhet): Arbeidsgiver = Arbeidsgiver(
        navn = virksomhet.navn,
        adresse = hentAdresseFraDtoAdresse(virksomhet.adresse!!),
        identifikator = lagIdentifikator(virksomhet.orgnr)
    )

    fun hentSelvstendig(sedData: SedDataDto): Selvstendig = Selvstendig(
        arbeidsgiver = sedData.selvstendigeVirksomheter!!.map {
            Arbeidsgiver(
                identifikator = lagIdentifikator(it.orgnr),
                adresse = hentAdresseFraDtoAdresse(it.adresse!!),
                navn = it.navn
            )
        }
    )

    fun formaterDato(dato: LocalDate): String = Konstanter.dateTimeFormatter.format(dato)

    fun hentAdresseFraDtoAdresse(sAdresse: no.nav.melosys.eessi.controller.dto.Adresse): Adresse = Adresse(
        gate = sAdresse.gateadresse,
        postnummer = sAdresse.postnr,
        by = sAdresse.poststed,
        land = LandkodeMapper.mapTilLandkodeIso2(sAdresse.land),
        bygning = sAdresse.tilleggsnavn,
        region = sAdresse.region
    ).also {
        if (!StringUtils.hasText(it.by) || !StringUtils.hasText(it.land)) {
            throw MappingException("Felter 'poststed' og 'land' er påkrevd for adresser")
        }
    }

    fun lagIdentifikator(orgnr: String?): List<Identifikator> =
        if (!StringUtils.hasText(orgnr)) {
            emptyList()
        } else {
            listOf(Identifikator(id = orgnr, type = "registrering"))
        }

    fun mapTilPeriodeDto(lovvalgsperiode: Lovvalgsperiode): Periode? =
        if (lovvalgsperiode.fom != null) {
            if (lovvalgsperiode.tom != null) {
                Periode(
                    fastperiode = Fastperiode(
                        startdato = formaterDato(lovvalgsperiode.fom!!),
                        sluttdato = formaterDato(lovvalgsperiode.tom!!)
                    )
                )
            } else {
                Periode(
                    aapenperiode = AapenPeriode(
                        startdato = formaterDato(lovvalgsperiode.fom!!)
                    )
                )
            }
        } else {
            null
        }

    fun landkodeIso2EllerNull(iso3: String?): String? = when {
        iso3 == null -> null
        iso3.length == 2 -> iso3
        else -> LandkodeMapper.mapTilLandkodeIso2(iso3)
    }
}
