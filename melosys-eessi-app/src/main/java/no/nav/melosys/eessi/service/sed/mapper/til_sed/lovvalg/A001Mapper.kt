package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import no.nav.melosys.eessi.controller.dto.Bestemmelse
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode
import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.Konstanter.DEFAULT_SED_G_VER
import no.nav.melosys.eessi.models.sed.Konstanter.SED_VER_CDM_4_4
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.*
import no.nav.melosys.eessi.models.sed.nav.*
import no.nav.melosys.eessi.service.sed.LandkodeMapper
import no.nav.melosys.eessi.service.sed.mapper.UnntakArtikkelMapper

class A001Mapper : LovvalgSedMapper<MedlemskapA001> {
    override fun getSedType(): SedType = SedType.A001

    override fun mapTilSed(sedData: SedDataDto): SED =
        super.mapTilSed(sedData).apply {
            sedVer = SED_VER_CDM_4_4
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
            forordning8832004 = lovvalgsperiode?.let { getForordning8832004(it) },
            rammeavtale = lovvalgsperiode?.let { getRammeavtale(it, sedData) }
        )
    }

    private fun getUnntak(lovvalgsperiode: Lovvalgsperiode): Unntak =
        Unntak(
            begrunnelse = lovvalgsperiode.unntaksBegrunnelse
        )

    private fun getForordning8832004(lovvalgsperiode: Lovvalgsperiode) = Forordning8832004(
        unntak = UnntakForordning(
            grunnlag = Grunnlag(
                artikkel = UnntakArtikkelMapper.mapFromBestemmelse(lovvalgsperiode.unntakFraBestemmelse),
                annet = if (UnntakArtikkelMapper.BESTEMMELSE_OTHER == lovvalgsperiode.unntakFraBestemmelse.toString()) "" else null
            )
        )
    )

    private fun getRammeavtale(lovvalgsperiode: Lovvalgsperiode, sedData: SedDataDto): Rammeavtale? {
        if (lovvalgsperiode.unntakFraBestemmelse != Bestemmelse.ART_13_1_a) return null

        return Rammeavtale(Fjernarbeid(if (sedData.erFjernarbeidTWFA == true) "ja" else "nei"))
    }

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
