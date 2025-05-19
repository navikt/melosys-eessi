package no.nav.melosys.eessi.service.sed.mapper.til_sed

import mu.KotlinLogging
import no.nav.melosys.eessi.controller.dto.*
import no.nav.melosys.eessi.controller.dto.Adresse
import no.nav.melosys.eessi.controller.dto.Arbeidssted
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.exception.MappingException
import no.nav.melosys.eessi.models.sed.Konstanter
import no.nav.melosys.eessi.models.sed.Konstanter.DEFAULT_SED_G_VER
import no.nav.melosys.eessi.models.sed.Konstanter.SED_VER_CDM_4_3
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.nav.*
import no.nav.melosys.eessi.models.sed.nav.Bruker
import no.nav.melosys.eessi.models.sed.nav.Periode
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper.finnLandkodeIso2
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper.mapTilLandkodeIso2
import org.springframework.util.StringUtils
import java.time.LocalDate

private val log = KotlinLogging.logger { }

/**
 * Felles mapper-interface for alle typer SED. Mapper NAV-objektet i NAV-SED, som brukes av eux for
 * å plukke ut nødvendig informasjon for en angitt SED.
 */
interface SedMapper {

    fun getSedType(): SedType

    fun mapTilSed(sedData: SedDataDto): SED {
        return SED(
            nav = prefillNav(sedData),
            sedType = getSedType().name,
            sedGVer = DEFAULT_SED_G_VER,
            sedVer = SED_VER_CDM_4_3
        )
    }

    fun prefillNav(sedData: SedDataDto): Nav {
        val erSedMatch = getSedType() in setOf(
            SedType.A001, SedType.A003,
            SedType.A008, SedType.A009, SedType.A010
        )
        return Nav(
            arbeidssted = null,
            arbeidsland = if (erSedMatch) hentArbeidsland(sedData).takeIf { it.isNotEmpty() } else null,
            harfastarbeidssted = if (!erSedMatch) null else if (sedData.harFastArbeidssted == true) "ja" else "nei",
            bruker = hentBruker(sedData),
            arbeidsgiver = hentArbeidsgivereILand(
                sedData.arbeidsgivendeVirksomheter,
                sedData.finnLovvalgslandDefaultNO()
            ),
            ytterligereinformasjon = sedData.ytterligereInformasjon,
            selvstendig = sedData.selvstendigeVirksomheter
                .takeIf { it.isNotEmpty() }
                ?.let { hentSelvstendig(sedData) }
        )
    }

    fun hentStatsborgerskap(sedDataDto: SedDataDto): List<Statsborgerskap> =
        sedDataDto.bruker.statsborgerskap
            .filter { finnLandkodeIso2(it).isPresent }
            .map { lagStatsborgerskap(it) }
            .onEach { statsborgerskap ->
                if (statsborgerskap.land == LandkodeMapper.KOSOVO_LANDKODE_ISO2) {
                    statsborgerskap.land = LandkodeMapper.UKJENT_LANDKODE_ISO2
                    log.info("Endrer statsborgerskap fra Kosovo til Ukjent. gsakSaksnummer: ${sedDataDto.gsakSaksnummer}")
                }
            }.apply {
                if (isEmpty()) {
                    throw MappingException(
                        "Statsborgerskap mangler eller er ugyldig. Statsborgerskap fra sedData: ${
                            sedDataDto.bruker.statsborgerskap.joinToString(
                                ", "
                            )
                        }"
                    )
                }
            }

    fun hentAdresser(sedDataDto: SedDataDto): List<no.nav.melosys.eessi.models.sed.nav.Adresse> =
        listOfNotNull(
            sedDataDto.bostedsadresse?.let { mapBostedsadresse(it) },
            sedDataDto.kontaktadresse?.let { mapAdresse(it) },
            sedDataDto.oppholdsadresse?.let { mapAdresse(it) }
        )

    fun hentArbeidsland(sedData: SedDataDto): List<no.nav.melosys.eessi.models.sed.nav.Arbeidsland> =
        sedData.arbeidsland.map { arbeidsland ->
            no.nav.melosys.eessi.models.sed.nav.Arbeidsland(
                land = arbeidsland.land,
                arbeidssted = hentArbeidssted4_3(arbeidsland.arbeidssted)
            )
        }

    fun hentArbeidsgivereILand(virksomheter: List<Virksomhet>, landkode: String?): List<Arbeidsgiver> =
        hentArbeidsgiver(virksomheter) { it.adresse.land == landkode }

    fun hentArbeidsgivereIkkeILand(virksomheter: List<Virksomhet>, landkode: String?): List<Arbeidsgiver> {
        return hentArbeidsgiver(virksomheter) { it.adresse.land != landkode }
    }

    fun mapTilPeriodeDto(lovvalgsperiode: Lovvalgsperiode): Periode? {
        val fom = lovvalgsperiode.fom ?: return null

        return Periode(
            fastperiode = lovvalgsperiode.tom?.let {
                Fastperiode(
                    startdato = fom.formater(),
                    sluttdato = it.formater()
                )
            },
            aapenperiode = if (lovvalgsperiode.tom == null) {
                AapenPeriode(
                    startdato = formaterDato(fom)
                )
            } else null
        )
    }

    private fun hentBruker(sedDataDto: SedDataDto) = Bruker(
        person = hentPerson(sedDataDto),
        adresse = hentAdresser(sedDataDto)
    ).apply {
        setFamiliemedlemmer(sedDataDto, this)
    }

    private fun hentPerson(sedData: SedDataDto): Person {
        val bruker = sedData.bruker
        return Person(
            fornavn = bruker.fornavn,
            etternavn = bruker.etternavn,
            foedselsdato = formaterDato(bruker.foedseldato),
            foedested = null, //det antas at ikke trengs når NAV fyller ut.
            kjoenn = Kjønn.valueOf(bruker.kjoenn),
            statsborgerskap = hentStatsborgerskap(sedData),
            pin = hentPin(sedData)
        )
    }

    private fun lagStatsborgerskap(landkode: String): Statsborgerskap = Statsborgerskap(mapTilLandkodeIso2(landkode))

    private fun hentPin(sedData: SedDataDto): List<Pin> {
        val brukerPin = Pin(
            identifikator = sedData.bruker.fnr,
            land = "NO",
            sektor = null // Sektor settes til null per nå. Ikke påkrevd.
        )

        val utenlandskIdent = sedData.utenlandskIdent
        val utenlandskPins = utenlandskIdent.map { (ident, landkode) ->
            Pin(
                identifikator = ident,
                land = mapTilLandkodeIso2(landkode),
                sektor = null
            )
        }

        return listOf(brukerPin) + utenlandskPins
    }

    private fun mapBostedsadresse(adresse: Adresse): no.nav.melosys.eessi.models.sed.nav.Adresse =
        mapAdresse(adresse).apply {
            if (adresse.adressetype == Adressetype.BOSTEDSADRESSE) {
                type = Adressetype.BOSTEDSADRESSE.adressetypeRina
            }
        }

    private fun mapAdresse(adresse: Adresse): no.nav.melosys.eessi.models.sed.nav.Adresse {
        val adressetype = adresse.adressetype ?: throw MappingException("Adresse.adressetype kan ikke være null")
        val landkodeIso3 = adresse.land ?: throw MappingException("Adresse.land kan ikke være null")

        return no.nav.melosys.eessi.models.sed.nav.Adresse(
            bygning = adresse.tilleggsnavn.tilEESSIMediumString(),
            type = adressetype.adressetypeRina,
            gate = adresse.gateadresse.tilEESSIMediumString(),
            by = adresse.poststed.tilEESSIMediumString(),
            postnummer = adresse.postnr.tilEESSIMediumString(),
            region = adresse.region.tilEESSIMediumString(),
            land = mapTilLandkodeIso2(landkodeIso3)
        )
    }

    private fun setFamiliemedlemmer(sedData: SedDataDto, bruker: Bruker) {
        //Splitter per nå navnet etter første mellomrom

        sedData.familieMedlem.find { it.relasjon.equals("FAR", ignoreCase = true) }?.let { farMedlem ->
            bruker.far = Far(person = Person(etternavnvedfoedsel = farMedlem.etternavn, fornavn = farMedlem.fornavn))
        }

        sedData.familieMedlem.find { it.relasjon.equals("MOR", ignoreCase = true) }?.let { morMedlem ->
            bruker.mor = Mor(person = Person(etternavnvedfoedsel = morMedlem.etternavn, fornavn = morMedlem.fornavn))
        }
    }

    private fun hentArbeidssted4_3(arbeidssteder: List<Arbeidssted>): List<no.nav.melosys.eessi.models.sed.nav.Arbeidssted> =
        arbeidssteder.map {
            no.nav.melosys.eessi.models.sed.nav.Arbeidssted(
                navn = it.navn,
                adresse = hentAdresseFraDtoAdresse(it.adresse),
                hjemmebase = landkodeIso2EllerNull(it.hjemmebase),
                erikkefastadresse = when {
                    it.fysisk -> "nei"
                    StringUtils.hasText(it.hjemmebase) || !it.fysisk -> "ja"
                    else -> null
                }
            )
        }

    private fun hentArbeidsgiver(
        virksomheter: List<Virksomhet>,
        virksomhetPredicate: (Virksomhet) -> Boolean
    ): List<Arbeidsgiver> =
        virksomheter
            .filter(virksomhetPredicate)
            .map { hentArbeidsgiver(it) }

    private fun hentArbeidsgiver(virksomhet: Virksomhet): Arbeidsgiver {
        return Arbeidsgiver(
            navn = virksomhet.navn,
            adresse = hentAdresseFraDtoAdresse(virksomhet.adresse),
            identifikator = lagIdentifikator(virksomhet.orgnr)
        )
    }

    private fun hentSelvstendig(sedData: SedDataDto) = Selvstendig(arbeidsgiver = sedData.selvstendigeVirksomheter.map {
        Arbeidsgiver(
            identifikator = lagIdentifikator(it.orgnr),
            adresse = hentAdresseFraDtoAdresse(it.adresse),
            navn = it.navn
        )
    })

    private fun hentAdresseFraDtoAdresse(adresse: Adresse) = no.nav.melosys.eessi.models.sed.nav.Adresse(
        gate = adresse.gateadresse.tilEESSIMediumString(),
        postnummer = adresse.postnr.tilEESSIMediumString(),
        by = adresse.poststed.tilEESSIMediumString(),
        land = mapTilLandkodeIso2(adresse.land).tilEESSIMediumString(),
        bygning = adresse.tilleggsnavn.tilEESSIMediumString(),
        region = adresse.region.tilEESSIMediumString()
    ).also {
        if (it.by.isNullOrBlank() || it.land.isNullOrBlank()) {
            throw MappingException("Felter 'poststed' og 'land' er påkrevd for adresser")
        }
    }

    private fun lagIdentifikator(orgnr: String?): List<Identifikator> {
        return if (orgnr.isNullOrBlank()) {
            emptyList()
        } else {
            listOf(Identifikator(id = orgnr, type = "registrering"))
        }
    }

    private fun landkodeIso2EllerNull(iso3: String?): String? {
        return when {
            iso3 == null -> null
            iso3.length == 2 -> iso3
            else -> mapTilLandkodeIso2(iso3)
        }
    }

    private fun formaterDato(dato: LocalDate): String = Konstanter.dateTimeFormatter.format(dato)

    fun LocalDate?.formater(): String =
        this?.let { formaterDato(it) } ?: throw MappingException("dato kan ikke være null")

    fun LocalDate?.formaterEllerNull(): String? = this?.let { formaterDato(it) }

    /**
     * Forbereder en streng for EESSI-overføring i henhold til EESSIMediumStringType krav.
     * Returnerer null hvis strengen er null, blank eller blir tom etter trimming.
     * Ellers returneres den trimmede strengen, forkortet til maksLengde om nødvendig.
     */
    fun String?.tilEESSIMediumString(maksLengde: Int = 155): String? {
        if (this.isNullOrBlank()) {
            return null
        }

        val trimmetVerdi = this.trim()

        if (trimmetVerdi.isEmpty()) {
            log.warn { "Strengen '${this}' er blank og blir ikke med i SED." }
            return null
        }

        return if (trimmetVerdi.length > maksLengde) {
            log.warn { "Strengen '${this}' er for lang og blir trimmet til $maksLengde tegn." }
            trimmetVerdi.substring(0, maksLengde)
        } else {
            trimmetVerdi
        }
    }
}
