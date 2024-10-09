package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag

import no.nav.melosys.eessi.controller.dto.Bestemmelse
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode
import no.nav.melosys.eessi.controller.dto.Periode
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.NyttLovvalgSedMapper

interface NyttLovvalgSedGrunnlagMapper<T : Medlemskap?> : NyttLovvalgSedMapper<T>, SedGrunnlagMapper {
    fun hentPeriode(medlemskap: T): Periode

    fun hentUnntakFraLovvalgsland(medlemskap: T): String? {
        return null
    }

    fun hentUnntakFraLovvalgsbestemmelse(medlemskap: T): String? {
        return null
    }

    fun hentLovvalgsperiode(medlemskap: T): Lovvalgsperiode {
        val periode = hentPeriode(medlemskap)

        val lovvalgsperiode = Lovvalgsperiode()
        lovvalgsperiode.fom = periode.fom
        lovvalgsperiode.tom = periode.tom
        lovvalgsperiode.lovvalgsland = hentLovvalgsland(medlemskap)
        lovvalgsperiode.bestemmelse = Bestemmelse.fraString(hentLovvalgsbestemmelse(medlemskap))
        lovvalgsperiode.unntakFraLovvalgsland = hentUnntakFraLovvalgsland(medlemskap)
        lovvalgsperiode.unntakFraBestemmelse = Bestemmelse.fraString(hentUnntakFraLovvalgsbestemmelse(medlemskap))

        return lovvalgsperiode
    }
}
