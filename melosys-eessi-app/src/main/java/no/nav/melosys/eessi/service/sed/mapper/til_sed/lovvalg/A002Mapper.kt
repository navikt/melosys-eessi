package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.exception.MappingException
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA002
import no.nav.melosys.eessi.models.sed.medlemskap.impl.UnntakA002
import no.nav.melosys.eessi.models.sed.medlemskap.impl.VedtakA002
import no.nav.melosys.eessi.models.sed.nav.Fastperiode
import no.nav.melosys.eessi.models.sed.nav.Periode

class A002Mapper : LovvalgSedMapper<MedlemskapA002> {

    override fun getSedType() = SedType.A002

    override fun getMedlemskap(sedData: SedDataDto): MedlemskapA002 {
        val svarAnmodningUnntak = sedData.svarAnmodningUnntak
            ?: throw MappingException("Trenger SvarAnmodningUnntak for å opprette A002")
        val beslutning = svarAnmodningUnntak.beslutning
            ?: throw MappingException("Beslutning mangler i SvarAnmodningUnntak")

        return MedlemskapA002(
            unntak = UnntakA002(
                vedtak = VedtakA002(
                    annenperiode = Periode(
                        fastperiode = Fastperiode(
                            startdato = svarAnmodningUnntak.delvisInnvilgetPeriode?.fom.formaterEllerNull(),
                            sluttdato = svarAnmodningUnntak.delvisInnvilgetPeriode?.tom.formaterEllerNull()
                        )
                    ),
                    begrunnelse = svarAnmodningUnntak.begrunnelse,
                    resultat = beslutning.rinaKode
                )
            )
        )
    }
}
