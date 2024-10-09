package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.melosys.eessi.controller.dto.*
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003
import no.nav.melosys.eessi.models.sed.nav.Arbeidsgiver
import no.nav.melosys.eessi.models.sed.nav.VedtakA003
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.io.IOException
import java.util.List
import java.util.function.Consumer
import java.util.function.Function

internal class SedGrunnlagMapperA003Test {
    private val sedGrunnlagMapper = SedGrunnlagMapperA003()

    @Test
    @Throws(IOException::class)
    fun map_medUtfyltNav_forventVerdier() {
        val sedGrunnlagDto: SedGrunnlagDto = sedGrunnlagMapper.map(hentSed())


        Assertions.assertThat(sedGrunnlagDto).isNotNull()
        Assertions.assertThat(sedGrunnlagDto.sedType).isEqualTo("A003")
        Assertions.assertThat(sedGrunnlagDto.utenlandskIdent)
            .`as`("Utenlandsk ident har rett felt")
            .extracting(
                Function<Ident, Any> { obj: Ident -> obj.ident },
                Function<Ident, Any> { obj: Ident -> obj.landkode },
                Function<Ident, Any> { obj: Ident -> obj.erUtenlandsk() })
            .containsExactlyInAnyOrder(Assertions.tuple("15225345345", "BG", true))

        Assertions.assertThat(sedGrunnlagDto.bostedsadresse)
            .`as`("Bostedsadresse har rett felt")
            .extracting(
                Function<Adresse, Any> { obj: Adresse -> obj.adressetype },
                Function<Adresse, Any> { obj: Adresse -> obj.land },
                Function<Adresse, Any> { obj: Adresse -> obj.gateadresse })
            .containsExactlyInAnyOrder(Adressetype.BOSTEDSADRESSE, "BE", "Testgate Testbyggnavn")

        Assertions.assertThat(sedGrunnlagDto.arbeidssteder)
            .`as`("Arbeidssteder har rett info")
            .extracting(
                Function<Arbeidssted, Any> { obj: Arbeidssted -> obj.navn },
                Function<Arbeidssted, Any> { obj: Arbeidssted -> obj.isFysisk },
                Function<Arbeidssted, Any> { obj: Arbeidssted -> obj.hjemmebase })
            .containsExactlyInAnyOrder(
                Assertions.tuple("Testarbeidsstednavn", false, "Testarbeidsstedbase"),
                Assertions.tuple("Testarbeidsstednavn2", true, "Testarbeidsstedbase2")
            )

        Assertions.assertThat(sedGrunnlagDto.arbeidssteder)
            .`as`("Arbeidssteder har rette adresser")
            .extracting<Adresse, RuntimeException> { obj: Arbeidssted -> obj.adresse }
            .extracting(
                Function<Adresse, Any> { obj: Adresse -> obj.land },
                Function<Adresse, Any> { obj: Adresse -> obj.postnr },
                Function<Adresse, Any> { obj: Adresse -> obj.poststed },
                Function<Adresse, Any> { obj: Adresse -> obj.region },
                Function<Adresse, Any> { obj: Adresse -> obj.gateadresse })
            .containsExactlyInAnyOrder(
                Assertions.tuple(
                    "EE",
                    "Testarbeidsstedpostkode",
                    "Testarbeidsstedby",
                    "Testarbeidsstedregion",
                    "Testarbeidsstedgate Testarbeidsstedbygning"
                ),
                Assertions.tuple("CY", null, "Testarbeidsstedby2", null, "Testarbeidsstedgate2 Testarbeidsstedbygning2")
            )

        Assertions.assertThat(sedGrunnlagDto.arbeidsgivendeVirksomheter)
            .`as`("Arbeidsgivende virksomheter har rett info")
            .extracting(
                Function<Virksomhet, Any> { obj: Virksomhet -> obj.navn },
                Function<Virksomhet, Any> { obj: Virksomhet -> obj.orgnr })
            .containsExactlyInAnyOrder(
                Assertions.tuple("EQUINOR ASA", "923609016"),
                Assertions.tuple("adf", "123321"),
                Assertions.tuple("swe", "123")
            )

        Assertions.assertThat(sedGrunnlagDto.arbeidsgivendeVirksomheter)
            .`as`("Arbeidsgivende virksomheter har rette adresser")
            .extracting<Adresse, RuntimeException> { obj: Virksomhet -> obj.adresse }
            .extracting(
                Function<Adresse, Any> { obj: Adresse -> obj.land },
                Function<Adresse, Any> { obj: Adresse -> obj.postnr },
                Function<Adresse, Any> { obj: Adresse -> obj.poststed },
                Function<Adresse, Any> { obj: Adresse -> obj.gateadresse })
            .containsExactlyInAnyOrder(
                Assertions.tuple("BE", "4035", "STAVANGER", "Forusbeen 50"),
                Assertions.tuple("BE", null, "by", ""),
                Assertions.tuple("SE", null, "stck", "")
            )

        Assertions.assertThat(sedGrunnlagDto.selvstendigeVirksomheter)
            .`as`("Selvstendige virksomheter har rett info")
            .extracting(
                Function<Virksomhet, Any> { obj: Virksomhet -> obj.navn },
                Function<Virksomhet, Any> { obj: Virksomhet -> obj.orgnr })
            .containsExactlyInAnyOrder(Assertions.tuple("Testselvstendignavn", "Testselvstendignummer"))

        Assertions.assertThat(sedGrunnlagDto.selvstendigeVirksomheter)
            .`as`("Selvstendige virksomheter har rette adresser")
            .extracting<Adresse, RuntimeException> { obj: Virksomhet -> obj.adresse }
            .extracting(
                Function<Adresse, Any> { obj: Adresse -> obj.land },
                Function<Adresse, Any> { obj: Adresse -> obj.postnr },
                Function<Adresse, Any> { obj: Adresse -> obj.poststed },
                Function<Adresse, Any> { obj: Adresse -> obj.region },
                Function<Adresse, Any> { obj: Adresse -> obj.gateadresse })
            .containsExactlyInAnyOrder(
                Assertions.tuple(
                    "BG",
                    "Testselvstendigpostkode",
                    "Testselvstendigby",
                    "Testselvstendigregion",
                    "Testselvstendiggate Testselvstendigbygning"
                )
            )
    }

    @Test
    @Throws(IOException::class)
    fun map_ingenBostedsadresse_forventPostadresse() {
        val sed = hentSed()
        val adresse = sed.nav!!.bruker!!.adresse!![0]
        adresse.type = Adressetype.POSTADRESSE.adressetypeRina
        sed.nav!!.bruker!!.adresse = List.of(adresse)

        val bostedsadresse = sedGrunnlagMapper.map(sed).bostedsadresse

        Assertions.assertThat(bostedsadresse)
            .extracting(
                Function<Adresse, Any> { obj: Adresse -> obj.adressetype },
                Function<Adresse, Any> { obj: Adresse -> obj.land },
                Function<Adresse, Any> { obj: Adresse -> obj.gateadresse })
            .containsExactlyInAnyOrder(Adressetype.POSTADRESSE, "BE", "Testgate Testbyggnavn")
    }

    @Test
    @Throws(IOException::class)
    fun map_ingenAdresse_forventTomAdresse() {
        val sed = hentSed()
        sed.nav!!.bruker!!.adresse = listOf()

        val bostedsadresse = sedGrunnlagMapper.map(sed).bostedsadresse

        Assertions.assertThat(bostedsadresse).isEqualTo(Adresse())
    }

    @Test
    @Throws(IOException::class)
    fun map_kunNorskIdent_forventTomListeAvUtenlandskeIdenter() {
        val sed = hentSed()
        val pin = sed.nav!!.bruker!!.person!!.pin.iterator().next()
        pin.land = "NO"
        sed.nav!!.bruker!!.person!!.pin = List.of(pin)

        val utenlandskIdent = sedGrunnlagMapper.map(sed).utenlandskIdent

        Assertions.assertThat(utenlandskIdent).isEmpty()
    }

    @Test
    @Throws(IOException::class)
    fun map_ingenGate_forventKunBygning() {
        val sed = hentSed()
        val adresse = sed.nav!!.bruker!!.adresse!![0]
        adresse.gate = null
        sed.nav!!.bruker!!.adresse = List.of(adresse)

        val gateadresse = sedGrunnlagMapper.map(sed).bostedsadresse.gateadresse

        Assertions.assertThat(gateadresse).isEqualTo("Testbyggnavn")
    }

    @Test
    @Throws(IOException::class)
    fun map_ingenBygning_forventKunGate() {
        val sed = hentSed()
        val adresse = sed.nav!!.bruker!!.adresse!![0]
        adresse.bygning = null
        sed.nav!!.bruker!!.adresse = List.of(adresse)

        val gateadresse = sedGrunnlagMapper.map(sed).bostedsadresse.gateadresse

        Assertions.assertThat(gateadresse).isEqualTo("Testgate")
    }

    @Test
    @Throws(IOException::class)
    fun map_ingenArbeidsgiverAdresse_forventIkkeNorskArbeidsgiver() {
        val settTomAdresse =
            Consumer { arbeidsgiver: Arbeidsgiver -> arbeidsgiver.adresse = null }

        val sed = hentSed()
        sed.nav!!.arbeidsgiver!!.forEach(settTomAdresse)
        (sed.medlemskap as MedlemskapA003).andreland!!.arbeidsgiver!!.forEach(settTomAdresse)


        val norskeArbeidsgivendeVirksomheter = sedGrunnlagMapper.map(sed).norskeArbeidsgivendeVirksomheter


        Assertions.assertThat(norskeArbeidsgivendeVirksomheter).isEmpty()
    }

    @Test
    @Throws(IOException::class)
    fun map_norskArbeidsgiverNullIdentifikator_forventIngenIdentifikator() {
        val sed = hentSed()
        (sed.medlemskap as MedlemskapA003).andreland!!.arbeidsgiver!!.iterator().next().identifikator = null

        val orgnr = sedGrunnlagMapper.map(sed).norskeArbeidsgivendeVirksomheter.iterator().next().orgnr

        Assertions.assertThat(orgnr).isNull()
    }

    @Test
    fun sedErEndring_ikkeOpprinneligVedtak_forventerErEndring_true() {
        val medlemskapA003 = lagA003MedlemskapForSedErEndringTest(IKKE_OPPRINNELIG_VEDTAK)

        val erEndring = sedGrunnlagMapper.sedErEndring(medlemskapA003)

        org.junit.jupiter.api.Assertions.assertTrue(erEndring)
    }

    @Test
    fun sedErEndring_opprinneligVedtak_forventerErEndring_true() {
        val medlemskapA003 = lagA003MedlemskapForSedErEndringTest(OPPRINNELIG_VEDTAK)

        val erEndring = sedGrunnlagMapper.sedErEndring(medlemskapA003)

        org.junit.jupiter.api.Assertions.assertFalse(erEndring)
    }

    companion object {
        private val IKKE_OPPRINNELIG_VEDTAK: String? = null
        private const val OPPRINNELIG_VEDTAK = "ja"
        private fun lagA003MedlemskapForSedErEndringTest(opprinneligVedtak: String?): MedlemskapA003 {
            val vedtakA003 = VedtakA003()
            vedtakA003.eropprinneligvedtak = opprinneligVedtak
            val medlemskapA003 = MedlemskapA003()
            medlemskapA003.vedtak = vedtakA003
            return medlemskapA003
        }

        @Throws(IOException::class)
        private fun hentSed(): SED {
            val jsonUrl = SedGrunnlagMapperA003Test::class.java.classLoader.getResource("mock/sedA003.json")
            return ObjectMapper().readValue(jsonUrl, SED::class.java)
        }
    }
}
