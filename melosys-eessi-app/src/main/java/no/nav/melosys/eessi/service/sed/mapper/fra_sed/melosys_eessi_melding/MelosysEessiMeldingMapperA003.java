package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding;

import no.nav.melosys.eessi.kafka.producers.model.Periode;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003;
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.FraSedA003Mapper;

class MelosysEessiMeldingMapperA003 extends FraSedA003Mapper implements NyttLovvalgEessiMeldingMapper<MedlemskapA003>  {
    @Override
    public Periode mapPeriode(MedlemskapA003 medlemskap) {
        var periode = hentPeriode(medlemskap.getVedtak().getGjelderperiode());
        return new Periode(periode.getFom(), periode.getTom());
    }
}
