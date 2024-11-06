package no.nav.melosys.eessi.service.sed.mapper.til_sed

import com.google.common.base.Objects
import com.google.common.collect.Lists
import no.nav.melosys.eessi.controller.dto.*
import no.nav.melosys.eessi.controller.dto.Adresse
import no.nav.melosys.eessi.controller.dto.Arbeidsland
import no.nav.melosys.eessi.controller.dto.Arbeidssted
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.exception.MappingException
import no.nav.melosys.eessi.models.sed.Konstanter
import no.nav.melosys.eessi.models.sed.Konstanter.DEFAULT_SED_G_VER
import no.nav.melosys.eessi.models.sed.Konstanter.DEFAULT_SED_VER
import no.nav.melosys.eessi.models.sed.Konstanter.SED_VER_CDM_4_3
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.nav.*
import no.nav.melosys.eessi.models.sed.nav.Bruker
import no.nav.melosys.eessi.models.sed.nav.Periode
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper.finnLandkodeIso2
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper.mapTilLandkodeIso2
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.util.StringUtils
import java.time.LocalDate
import java.util.function.Predicate

/**
 * Felles mapper-interface for alle typer SED. Mapper NAV-objektet i NAV-SED, som brukes av eux for
 * å plukke ut nødvendig informasjon for en angitt SED.
 */
interface SedMapper {
    fun mapTilSed(sedData: SedDataDto, erCDM4_3: Boolean): SED {
        val sed = SED()
        sed.nav = prefillNav(sedData, erCDM4_3)
        sed.sedType = getSedType().name
        sed.sedGVer = DEFAULT_SED_G_VER

        if (erCDM4_3) {
            sed.sedVer = SED_VER_CDM_4_3
        } else {
            sed.sedVer = DEFAULT_SED_VER
        }


        return sed
    }

    fun getSedType(): SedType

    fun prefillNav(sedData: SedDataDto, erCDM4_3: Boolean): Nav {
        val nav = Nav()
        val sedType = getSedType()

        val harFastArbeidssted = sedData.harFastArbeidssted != null && sedData.harFastArbeidssted!!

        if (erCDM4_3) {
            when (sedType) {
                SedType.A001, SedType.A002, SedType.A003, SedType.A008, SedType.A009, SedType.A010 -> {
                    val arbeidsland = hentArbeidsland(sedData)
                    if (!arbeidsland.isEmpty()) {
                        nav.arbeidsland = arbeidsland
                        nav.harfastarbeidssted = if (harFastArbeidssted) "ja" else "nei"
                    }
                }

                else -> nav.arbeidssted = hentArbeidssted(sedData)
            }
        } else {
            nav.arbeidssted = hentArbeidssted(sedData)
        }

        nav.bruker = hentBruker(sedData)
        nav.arbeidsgiver = hentArbeidsgivereILand(sedData.arbeidsgivendeVirksomheter!!, sedData.finnLovvalgslandDefaultNO())
        nav.ytterligereinformasjon = sedData.ytterligereInformasjon

        if (!sedData.selvstendigeVirksomheter!!.isEmpty()) {
            nav.selvstendig = hentSelvstendig(sedData)
        }

        return nav
    }

    fun hentBruker(sedDataDto: SedDataDto): Bruker {
        val bruker = Bruker()
        bruker.person = hentPerson(sedDataDto)
        bruker.adresse = hentAdresser(sedDataDto)
        setFamiliemedlemmer(sedDataDto, bruker)
        return bruker
    }

    fun hentPerson(sedData: SedDataDto): Person {
        val person = Person()

        person.fornavn = sedData.bruker!!.fornavn
        person.etternavn = sedData.bruker!!.etternavn
        person.foedselsdato = formaterDato(sedData.bruker!!.foedseldato!!)
        person.foedested = null //det antas at ikke trengs når NAV fyller ut.
        person.kjoenn = Kjønn.valueOf(sedData.bruker!!.kjoenn!!)
        person.statsborgerskap = hentStatsborgerskap(sedData)
        person.pin = hentPin(sedData)

        return person
    }

    fun hentStatsborgerskap(sedDataDto: SedDataDto): List<Statsborgerskap?> {
        val statsborgerskapStringListe = sedDataDto.bruker!!.statsborgerskap
        val statsborgerskapList = statsborgerskapStringListe!!.stream()
            .filter { landkodeIso3: String? -> finnLandkodeIso2(landkodeIso3).isPresent }
            .map { landkode: String -> this.lagStatsborgerskap(landkode) }
            .toList()
        for (statsborgerskap in statsborgerskapList) {
            if (statsborgerskap.land != null && statsborgerskap.land == LandkodeMapper.KOSOVO_LANDKODE_ISO2) {
                statsborgerskap.land = LandkodeMapper.UKJENT_LANDKODE_ISO2
                log.info("Endrer statsborgerskap fra Kosovo til Ukjent. gsakSaksnummer: {}", sedDataDto.gsakSaksnummer)
            }
        }
        if (statsborgerskapList.isEmpty()) {
            throw MappingException(
                "Statsborgerskap mangler eller er ugyldig. statsborgerskap fra sedData:" +
                    java.lang.String.join(", ", statsborgerskapStringListe)
            )
        }
        return statsborgerskapList
    }

    private fun lagStatsborgerskap(landkode: String): Statsborgerskap {
        return Statsborgerskap(mapTilLandkodeIso2(landkode))
    }

    fun hentPin(sedData: SedDataDto): List<Pin> {
        val pins: MutableList<Pin> = Lists.newArrayList()

        pins.add(
            Pin(
                sedData.bruker!!.fnr, "NO",
                null
            )
        ) //null settes for sektor per nå. Ikke påkrevd. Evt hardkode 'alle'

        for ((ident, landkode) in sedData.utenlandskIdent!!) {
            pins.add(
                Pin(
                    ident,
                    mapTilLandkodeIso2(landkode), null
                )
            )
        }

        return pins
    }

    fun hentAdresser(sedDataDto: SedDataDto): List<no.nav.melosys.eessi.models.sed.nav.Adresse> {
        val adresser: MutableList<no.nav.melosys.eessi.models.sed.nav.Adresse> = ArrayList()
        if (sedDataDto.bostedsadresse != null) {
            adresser.add(mapBostedsadresse(sedDataDto.bostedsadresse!!))
        }
        if (sedDataDto.kontaktadresse != null) {
            adresser.add(mapAdresse(sedDataDto.kontaktadresse!!))
        }
        if (sedDataDto.oppholdsadresse != null) {
            adresser.add(mapAdresse(sedDataDto.oppholdsadresse!!))
        }
        return adresser
    }

    private fun mapBostedsadresse(adresse: Adresse): no.nav.melosys.eessi.models.sed.nav.Adresse {
        val bostedsadresse = mapAdresse(adresse)
        if (adresse.adressetype == Adressetype.BOSTEDSADRESSE) {
            bostedsadresse.type = Adressetype.BOSTEDSADRESSE.adressetypeRina
        }
        return bostedsadresse
    }

    private fun mapAdresse(adresse: Adresse): no.nav.melosys.eessi.models.sed.nav.Adresse {
        val bostedsadresse = no.nav.melosys.eessi.models.sed.nav.Adresse()
        bostedsadresse.type = adresse.adressetype!!.adressetypeRina
        bostedsadresse.gate = adresse.gateadresse
        bostedsadresse.by = adresse.poststed
        bostedsadresse.postnummer = adresse.postnr
        bostedsadresse.region = adresse.region
        bostedsadresse.land = mapTilLandkodeIso2(adresse.land)
        return bostedsadresse
    }

    fun setFamiliemedlemmer(sedData: SedDataDto, bruker: Bruker) {
        //Splitter per nå navnet etter første mellomrom

        val optionalFar = sedData.familieMedlem!!.stream()
            .filter { f: FamilieMedlem -> f.relasjon.equals("FAR", ignoreCase = true) }.findFirst()

        if (optionalFar.isPresent) {
            val far = Far()
            val person = Person()
            person.etternavnvedfoedsel = optionalFar.get().etternavn
            person.fornavn = optionalFar.get().fornavn

            far.person = person
            bruker.far = far
        }

        val optionalMor = sedData.familieMedlem!!.stream()
            .filter { f: FamilieMedlem -> f.relasjon.equals("MOR", ignoreCase = true) }.findFirst()

        if (optionalMor.isPresent) {
            val mor = Mor()
            val person = Person()
            person.etternavnvedfoedsel = optionalMor.get().etternavn
            person.fornavn = optionalMor.get().fornavn

            mor.person = person
            bruker.mor = mor
        }
    }

    fun hentArbeidsland(sedData: SedDataDto): List<no.nav.melosys.eessi.models.sed.nav.Arbeidsland> {
        return sedData.arbeidsland!!.stream().map { arbeidsland: Arbeidsland ->
            val arbeidslandSed = no.nav.melosys.eessi.models.sed.nav.Arbeidsland()
            arbeidslandSed.land = arbeidsland.land
            arbeidslandSed.arbeidssted = hentArbeidssted4_3(arbeidsland.arbeidssted)
            arbeidslandSed
        }.toList()
    }


    fun hentHarfastarbeidssted(sedData: SedDataDto): Boolean? {
        return sedData.harFastArbeidssted
    }

    fun hentArbeidssted4_3(arbeidssteder: List<Arbeidssted>): List<no.nav.melosys.eessi.models.sed.nav.Arbeidssted> {
        val arbeidsstedList: MutableList<no.nav.melosys.eessi.models.sed.nav.Arbeidssted> = Lists.newArrayList()

        for ((navn, adresse, fysisk, hjemmebase) in arbeidssteder) {
            val arbeidssted = no.nav.melosys.eessi.models.sed.nav.Arbeidssted()
            arbeidssted.navn = navn
            arbeidssted.adresse = hentAdresseFraDtoAdresse(adresse!!)
            arbeidssted.hjemmebase = landkodeIso2EllerNull(hjemmebase)

            if (fysisk) {
                arbeidssted.erikkefastadresse = "nei"
            } else if (StringUtils.hasText(arbeidssted.hjemmebase) || !fysisk) {
                arbeidssted.erikkefastadresse = "ja"
            }

            arbeidsstedList.add(arbeidssted)
        }

        return arbeidsstedList
    }

    fun hentArbeidssted(sedData: SedDataDto): List<no.nav.melosys.eessi.models.sed.nav.Arbeidssted> {
        val arbeidsstedList: MutableList<no.nav.melosys.eessi.models.sed.nav.Arbeidssted> = Lists.newArrayList()

        for ((navn, adresse, fysisk, hjemmebase) in sedData.arbeidssteder!!) {
            val arbeidssted = no.nav.melosys.eessi.models.sed.nav.Arbeidssted()
            arbeidssted.navn = navn
            arbeidssted.adresse = hentAdresseFraDtoAdresse(adresse!!)
            arbeidssted.hjemmebase = landkodeIso2EllerNull(hjemmebase)

            if (fysisk) {
                arbeidssted.erikkefastadresse = "nei"
            } else if (StringUtils.hasText(arbeidssted.hjemmebase) || !fysisk) {
                arbeidssted.erikkefastadresse = "ja"
            }

            arbeidsstedList.add(arbeidssted)
        }

        return arbeidsstedList
    }

    fun hentArbeidsgivereILand(virksomheter: List<Virksomhet>, landkode: String?): List<Arbeidsgiver> {
        return hentArbeidsgiver(
            virksomheter
        ) { v: Virksomhet -> Objects.equal(v.adresse!!.land, landkode) }
    }

    fun hentArbeidsgivereIkkeILand(virksomheter: List<Virksomhet>, landkode: String?): List<Arbeidsgiver> {
        return hentArbeidsgiver(
            virksomheter
        ) { v: Virksomhet -> !Objects.equal(v.adresse!!.land, landkode) }
    }

    fun hentArbeidsgiver(virksomheter: List<Virksomhet>, virksomhetPredicate: Predicate<Virksomhet>?): List<Arbeidsgiver> {
        return virksomheter.stream()
            .filter(virksomhetPredicate)
            .map { virksomhet: Virksomhet -> this.hentArbeidsgiver(virksomhet) }
            .toList()
    }

    fun hentArbeidsgiver(virksomhet: Virksomhet): Arbeidsgiver {
        val arbeidsgiver = Arbeidsgiver()
        arbeidsgiver.navn = virksomhet.navn
        arbeidsgiver.adresse = hentAdresseFraDtoAdresse(virksomhet.adresse!!)
        arbeidsgiver.identifikator = lagIdentifikator(virksomhet.orgnr)
        return arbeidsgiver
    }

    fun hentSelvstendig(sedData: SedDataDto): Selvstendig {
        val selvstendig = Selvstendig()
        val arbeidsgiverList: MutableList<Arbeidsgiver> = Lists.newArrayList()

        for ((navn, adresse, orgnr) in sedData.selvstendigeVirksomheter!!) {
            val arbeidsgiver = Arbeidsgiver()

            arbeidsgiver.identifikator = lagIdentifikator(orgnr)
            arbeidsgiver.adresse = hentAdresseFraDtoAdresse(adresse!!)
            arbeidsgiver.navn = navn

            arbeidsgiverList.add(arbeidsgiver)
        }

        selvstendig.arbeidsgiver = arbeidsgiverList

        return selvstendig
    }

    fun formaterDato(dato: LocalDate): String {
        return Konstanter.dateTimeFormatter.format(dato)
    }

    fun hentAdresseFraDtoAdresse(sAdresse: Adresse): no.nav.melosys.eessi.models.sed.nav.Adresse {
        val adresse = no.nav.melosys.eessi.models.sed.nav.Adresse()
        adresse.gate = sAdresse.gateadresse
        adresse.postnummer = sAdresse.postnr
        adresse.by = sAdresse.poststed
        adresse.land = mapTilLandkodeIso2(sAdresse.land)
        adresse.bygning = sAdresse.tilleggsnavn
        adresse.region = sAdresse.region

        if (!StringUtils.hasText(adresse.by) || !StringUtils.hasText(adresse.land)) {
            throw MappingException("Felter 'poststed' og 'land' er påkrevd for adresser")
        }

        return adresse
    }

    fun lagIdentifikator(orgnr: String?): List<Identifikator> {
        if (!StringUtils.hasText(orgnr)) {
            return emptyList()
        }

        val orgNr = Identifikator()
        orgNr.id = orgnr
        orgNr.type = "registrering"
        return java.util.List.of(orgNr)
    }

    fun mapTilPeriodeDto(lovvalgsperiode: Lovvalgsperiode): Periode? {
        val periode = Periode()

        if (lovvalgsperiode.fom != null) {
            if (lovvalgsperiode.tom != null) {
                val fastperiode = Fastperiode()
                fastperiode.startdato = formaterDato(lovvalgsperiode.fom!!)
                fastperiode.sluttdato = formaterDato(lovvalgsperiode.tom!!)
                periode.fastperiode = fastperiode
            } else {
                val aapenPeriode = AapenPeriode()
                aapenPeriode.startdato = formaterDato(lovvalgsperiode.fom!!)
                periode.aapenperiode = aapenPeriode
            }
        } else {
            return null
        }

        return periode
    }

    fun landkodeIso2EllerNull(iso3: String?): String? {
        return if (iso3 == null) {
            null
        } else if (iso3.length == 2) {
            iso3
        } else {
            mapTilLandkodeIso2(iso3)
        }
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(SedMapper::class.java)
    }
}
