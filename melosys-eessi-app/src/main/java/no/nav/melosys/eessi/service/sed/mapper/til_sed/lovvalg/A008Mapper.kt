package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import io.getunleash.Unleash
import no.nav.melosys.eessi.config.featuretoggle.ToggleName.CDM_4_4
import no.nav.melosys.eessi.controller.dto.A008Formaal
import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA008
import no.nav.melosys.eessi.models.sed.nav.*
import no.nav.melosys.eessi.service.sed.LandkodeMapper.mapTilLandkodeIso2
import org.slf4j.LoggerFactory

class A008Mapper(private val unleash: Unleash) : LovvalgSedMapper<MedlemskapA008> {

    private val log = LoggerFactory.getLogger(A008Mapper::class.java)

    override fun getSedType() = SedType.A008

    override fun getMedlemskap(sedData: SedDataDto) = MedlemskapA008(
        bruker = hentA008Bruker(sedData),
        formaal = hentFormaal(sedData)
    )

    private fun hentFormaal(sedData: SedDataDto): String? {
        val formaal = sedData.a008Formaal

        if (!unleash.isEnabled(CDM_4_4)) {
            if (formaal != null) {
                log.warn("a008Formaal mottatt fra melosys-web men CDM 4.4 toggle er deaktivert. Ignorerer formaal: {}", formaal.rinaVerdi)
            }
            return null
        }
        if (formaal == null) {
            log.warn("a008Formaal er ikke satt i melosys-web for A008 SED når CDM 4.4 er aktivert")
            return A008Formaal.ARBEID_FLERE_LAND.rinaVerdi
        }
        return formaal.rinaVerdi
    }

    override fun prefillNav(sedData: SedDataDto): Nav =
        super.prefillNav(sedData).apply {
            if (sedData.arbeidsland.isEmpty()) {
                harfastarbeidssted = null
            }
        }

    override fun hentArbeidsland(sedData: SedDataDto): List<Arbeidsland> {
        val arbeidslandListe = super.hentArbeidsland(sedData).toMutableList()

        if (unleash.isEnabled(CDM_4_4)) {
            if (arbeidslandListe.isEmpty()) {
                // TODO: Hardkodet FI/Helsinki for testing - fjern før merge
                arbeidslandListe.add(Arbeidsland(bosted = ArbeidslandBosted(adresse = Adresse(land = "FI", by = "Helsinki"))))
            } else {
                arbeidslandListe.firstOrNull()?.bosted = lagArbeidslandBosted(sedData)
            }

            arbeidslandListe.flatMap { it.arbeidssted }.forEach { arbeidssted ->
                arbeidssted.adresse?.navn = arbeidssted.navn
            }
        }

        return arbeidslandListe
    }

    private fun lagArbeidslandBosted(sedData: SedDataDto): ArbeidslandBosted? {
        val bostedsland = sedData.avklartBostedsland?.let { mapTilLandkodeIso2(it) }
            ?: return null

        val adresse = sedData.bostedsadresse?.let {
            Adresse(
                by = it.poststed.tilEESSIShortString(),
                land = mapTilLandkodeIso2(it.land)
            )
        } ?: Adresse(land = bostedsland, by = "N/A")

        return ArbeidslandBosted(adresse = adresse)
    }

    private fun hentA008Bruker(sedData: SedDataDto): MedlemskapA008Bruker {
        val arbeidIFlereLand = ArbeidIFlereLand(
            bosted = Bosted(sedData.avklartBostedsland?.let { mapTilLandkodeIso2(it) }),
            yrkesaktivitet = sedData.søknadsperiode?.fom?.let { søknadsperiodeFom ->
                Yrkesaktivitet(startdato = søknadsperiodeFom.formaterEllerNull())
            }
        )

        return MedlemskapA008Bruker(
            arbeidiflereland = if (unleash.isEnabled(CDM_4_4)) listOf(arbeidIFlereLand) else arbeidIFlereLand
        )
    }
}
