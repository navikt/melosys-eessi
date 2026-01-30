package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import io.getunleash.Unleash
import no.nav.melosys.eessi.config.featuretoggle.ToggleName.CDM_4_4
import no.nav.melosys.eessi.controller.dto.A008Formaal
import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.Konstanter.DEFAULT_SED_G_VER
import no.nav.melosys.eessi.models.sed.Konstanter.SED_VER_CDM_4_3
import no.nav.melosys.eessi.models.sed.Konstanter.SED_VER_CDM_4_4
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA008
import no.nav.melosys.eessi.models.sed.nav.*
import org.slf4j.LoggerFactory

class A008Mapper(private val unleash: Unleash) : LovvalgSedMapper<MedlemskapA008> {

    private val log = LoggerFactory.getLogger(A008Mapper::class.java)

    override fun getSedType() = SedType.A008

    override fun mapTilSed(sedData: SedDataDto): SED =
        super.mapTilSed(sedData).apply {
            sedVer = if (unleash.isEnabled(CDM_4_4)) SED_VER_CDM_4_4 else SED_VER_CDM_4_3
            sedGVer = DEFAULT_SED_G_VER
        }

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
            if (arbeidsland == null) {
                harfastarbeidssted = null
            }
        }

    private fun hentA008Bruker(sedData: SedDataDto) =
        MedlemskapA008Bruker(
            arbeidiflereland = ArbeidIFlereLand(
                bosted = Bosted(sedData.avklartBostedsland),
                yrkesaktivitet = sedData.søknadsperiode?.fom?.let { søknadsperiodeFom ->
                    Yrkesaktivitet(startdato = søknadsperiodeFom.formaterEllerNull())
                }
            ))
}
