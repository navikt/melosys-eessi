package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import io.getunleash.Unleash
import no.nav.melosys.eessi.config.featuretoggle.ToggleName.CDM_4_4
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode
import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.Konstanter.DEFAULT_SED_G_VER
import no.nav.melosys.eessi.models.sed.Konstanter.SED_VER_CDM_4_3
import no.nav.melosys.eessi.models.sed.Konstanter.SED_VER_CDM_4_4
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.*
import no.nav.melosys.eessi.models.sed.nav.*
import no.nav.melosys.eessi.service.sed.LandkodeMapper
import no.nav.melosys.eessi.service.sed.mapper.UnntakArtikkelMapper

class A001Mapper(private val unleash: Unleash) : LovvalgSedMapper<MedlemskapA001> {
    override fun getSedType(): SedType = SedType.A001

    override fun mapTilSed(sedData: SedDataDto): SED =
        super.mapTilSed(sedData).apply {
            sedVer = if (unleash.isEnabled(CDM_4_4)) SED_VER_CDM_4_4 else SED_VER_CDM_4_3
            sedGVer = DEFAULT_SED_G_VER
        }

    override fun getMedlemskap(sedData: SedDataDto): MedlemskapA001 {
        val lovvalgsperiode = sedData.finnLovvalgsperiode()

        return MedlemskapA001(
            unntak = lovvalgsperiode?.let { getUnntak(it) },
            naavaerendemedlemskap = lovvalgsperiode?.let { getUnntakFraLovvalgsland(it) }.orEmpty().toMutableList(),
            forespurtmedlemskap = lovvalgsperiode?.let { getLovvalgsland(it) }.orEmpty().toMutableList(),
            soeknadsperiode = lovvalgsperiode?.let { getSoeknadsperiode(it) },
            tidligereperiode = sedData.tidligereLovvalgsperioder.mapNotNull { mapTilPeriodeDto(it) }.toMutableList(),
            vertsland = getVertsland(sedData),
            anmodning = getAnmodning(),
            forordning8832004 = if (unleash.isEnabled(CDM_4_4)) lovvalgsperiode?.let { getForordning8832004(it, sedData) } else null
        )
    }

    private fun getUnntak(lovvalgsperiode: Lovvalgsperiode): Unntak {
        if (unleash.isEnabled(CDM_4_4)) {
            return Unntak(
                begrunnelse = lovvalgsperiode.unntaksBegrunnelse
            )
        }

        return Unntak(
            begrunnelse = lovvalgsperiode.unntaksBegrunnelse,
            grunnlag = Grunnlag(
                artikkel = UnntakArtikkelMapper.mapFromBestemmelse(lovvalgsperiode.unntakFraBestemmelse),
                annet = if (UnntakArtikkelMapper.BESTEMMELSE_OTHER == lovvalgsperiode.unntakFraBestemmelse.toString()) "" else null
            )
        )
    }

    private fun getForordning8832004(lovvalgsperiode: Lovvalgsperiode, sedData: SedDataDto) = Forordning8832004(
        unntak = UnntakForordning(
            grunnlag = Grunnlag(
                artikkel = UnntakArtikkelMapper.mapFromBestemmelse(lovvalgsperiode.unntakFraBestemmelse),
                annet = if (UnntakArtikkelMapper.BESTEMMELSE_OTHER == lovvalgsperiode.unntakFraBestemmelse.toString()) "" else null
            )
        ),
        artikkel10 = if (sedData.erFjernarbeidTWFA == true) Artikkel10(enighet = Enighet(eessiYesNoType = "1")) else null
    )

    private fun getVertsland(sedData: SedDataDto) = Vertsland(
        arbeidsgiver = hentArbeidsgivereIkkeILand(
            virksomheter = sedData.arbeidsgivendeVirksomheter,
            landkode = sedData.finnLovvalgslandDefaultNO()
        )
    )

    private fun getUnntakFraLovvalgsland(lovvalgsperiode: Lovvalgsperiode) = listOf(
        Land(
            landkode = LandkodeMapper.mapTilLandkodeIso2(lovvalgsperiode.unntakFraLovvalgsland)
        )
    )

    private fun getLovvalgsland(lovvalgsperiode: Lovvalgsperiode) = listOf(
        Land(
            landkode = LandkodeMapper.mapTilLandkodeIso2(lovvalgsperiode.lovvalgsland)
        )
    )

    private fun getSoeknadsperiode(lovvalgsperiode: Lovvalgsperiode): Fastperiode? =
        mapTilPeriodeDto(lovvalgsperiode)?.fastperiode

    // Blir ikke implementert i denne versjonen av Melosys.
    private fun getAnmodning() = Anmodning(erendring = "nei")  // Hardkodes til "nei" inntil videre
}
