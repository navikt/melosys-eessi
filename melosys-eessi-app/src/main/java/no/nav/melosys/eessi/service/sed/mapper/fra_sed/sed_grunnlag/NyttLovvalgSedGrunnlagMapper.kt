package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.Periode;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.NyttLovvalgSedMapper;

public interface NyttLovvalgSedGrunnlagMapper<T extends Medlemskap> extends NyttLovvalgSedMapper<T>, SedGrunnlagMapper {

    Periode hentPeriode(T medlemskap);

    default String hentUnntakFraLovvalgsland(T medlemskap) {
        return null;
    }

    default String hentUnntakFraLovvalgsbestemmelse(T medlemskap) {
        return null;
    }

    default Lovvalgsperiode hentLovvalgsperiode(T medlemskap) {
        Periode periode = hentPeriode(medlemskap);

        Lovvalgsperiode lovvalgsperiode = new Lovvalgsperiode();
        lovvalgsperiode.setFom(periode.getFom());
        lovvalgsperiode.setTom(periode.getTom());
        lovvalgsperiode.setLovvalgsland(hentLovvalgsland(medlemskap));
        lovvalgsperiode.setBestemmelse(Bestemmelse.fraString(hentLovvalgsbestemmelse(medlemskap)));
        lovvalgsperiode.setUnntakFraLovvalgsland(hentUnntakFraLovvalgsland(medlemskap));
        lovvalgsperiode.setUnntakFraBestemmelse(Bestemmelse.fraString(hentUnntakFraLovvalgsbestemmelse(medlemskap)));

        return lovvalgsperiode;
    }
}
