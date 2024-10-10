package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag

import no.nav.melosys.eessi.controller.dto.Bestemmelse
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode
import no.nav.melosys.eessi.controller.dto.Periode
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.NyttLovvalgSedMapper

interface NyttLovvalgSedGrunnlagMapper<T : Medlemskap?> : NyttLovvalgSedMapper<T>, SedGrunnlagMapper {
    fun hentPeriode(medlemskap: T): Periode

    fun hentUnntakFraLovvalgsland(medlemskap: T): String? = null

    fun hentUnntakFraLovvalgsbestemmelse(medlemskap: T): String? = null

    fun hentLovvalgsperiode(medlemskap: T): Lovvalgsperiode {
        val periode = hentPeriode(medlemskap)

        return Lovvalgsperiode().apply {
            fom = periode.fom
            tom = periode.tom
            lovvalgsland = hentLovvalgsland(medlemskap)
            bestemmelse = Bestemmelse.fraString(hentLovvalgsbestemmelse(medlemskap))
            unntakFraLovvalgsland = hentUnntakFraLovvalgsland(medlemskap)
            unntakFraBestemmelse = Bestemmelse.fraString(hentUnntakFraLovvalgsbestemmelse(medlemskap))
        }
    }
}
