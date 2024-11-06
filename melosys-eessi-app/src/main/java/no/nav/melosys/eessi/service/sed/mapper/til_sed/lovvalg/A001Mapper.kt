package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode
import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA001
import no.nav.melosys.eessi.models.sed.nav.*
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper
import no.nav.melosys.eessi.service.sed.helpers.UnntakArtikkelMapper

class A001Mapper : LovvalgSedMapper<MedlemskapA001> {

    override fun getMedlemskap(sedData: SedDataDto): MedlemskapA001 {
        val medlemskap = MedlemskapA001()
        val lovvalgsperiode = sedData.finnLovvalgsperiode()

        if (lovvalgsperiode.isPresent) {
            medlemskap.unntak = getUnntak(lovvalgsperiode.get())
            medlemskap.naavaerendemedlemskap = getUnntakFraLovvalgsland(lovvalgsperiode.get()).toMutableList()
            medlemskap.forespurtmedlemskap = getLovvalgsland(lovvalgsperiode.get()).toMutableList()
            medlemskap.soeknadsperiode = getSoeknadsperiode(lovvalgsperiode.get())
            medlemskap.tidligereperiode = getTidligerePeriode(sedData.tidligereLovvalgsperioder).toMutableList()
        }

        medlemskap.vertsland = getVertsland(sedData)
        medlemskap.anmodning = getAnmodning()

        return medlemskap
    }

    private fun getUnntak(lovvalgsperiode: Lovvalgsperiode): Unntak {
        val unntak = Unntak()
        unntak.begrunnelse = lovvalgsperiode.unntaksBegrunnelse

        val grunnlag = Grunnlag()
        grunnlag.artikkel = UnntakArtikkelMapper.mapFromBestemmelse(lovvalgsperiode.unntakFraBestemmelse)

        if (UnntakArtikkelMapper.BESTEMMELSE_OTHER == grunnlag.annet) {
            grunnlag.annet = ""
        }
        unntak.grunnlag = grunnlag

        return unntak
    }

    private fun getVertsland(sedData: SedDataDto): Vertsland {
        val lovvalgsland = sedData.finnLovvalgslandDefaultNO()
        val vertsland = Vertsland()
        vertsland.arbeidsgiver = hentArbeidsgivereIkkeILand(sedData.arbeidsgivendeVirksomheter!!, lovvalgsland)

        return vertsland
    }

    private fun getUnntakFraLovvalgsland(lovvalgsperiode: Lovvalgsperiode): List<Land> {
        val land = Land()
        land.landkode = LandkodeMapper.mapTilLandkodeIso2(lovvalgsperiode.unntakFraLovvalgsland)

        return listOf(land)
    }

    private fun getLovvalgsland(lovvalgsperiode: Lovvalgsperiode): List<Land> {
        val land = Land()
        land.landkode = LandkodeMapper.mapTilLandkodeIso2(lovvalgsperiode.lovvalgsland)

        return listOf(land)
    }

    private fun getSoeknadsperiode(lovvalgsperiode: Lovvalgsperiode): Fastperiode {
        return mapTilPeriodeDto(lovvalgsperiode)!!.fastperiode!!
    }

    private fun getTidligerePeriode(tidligereLovvalgsperioder: List<Lovvalgsperiode>?): List<Periode> {
        return tidligereLovvalgsperioder?.mapNotNull { mapTilPeriodeDto(it) } ?: emptyList()
    }

    private fun getAnmodning(): Anmodning {
        val anmodning = Anmodning()
        anmodning.erendring = "nei"

        return anmodning
    }

    override fun getSedType(): SedType {
        return SedType.A001
    }
}
