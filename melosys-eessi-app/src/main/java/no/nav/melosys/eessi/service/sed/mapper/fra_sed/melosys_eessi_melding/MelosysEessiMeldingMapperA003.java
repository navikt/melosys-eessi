package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding;

import no.nav.melosys.eessi.kafka.producers.model.Periode;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003;
import no.nav.melosys.eessi.service.sed.helpers.ErOpprinneligVedtakMapper;
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.FraSedA003Mapper;
import org.hibernate.internal.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.hibernate.internal.util.StringHelper.isEmpty;

class MelosysEessiMeldingMapperA003 extends FraSedA003Mapper implements NyttLovvalgEessiMeldingMapper<MedlemskapA003>  {
    @Override
    public Periode mapPeriode(MedlemskapA003 medlemskap) {
        var periode = hentPeriode(medlemskap.getVedtak().getGjelderperiode());
        return new Periode(periode.getFom(), periode.getTom());
    }

    @Override
    public Optional<Boolean> mapErOpprinneligVedtak(MedlemskapA003 medlemskap) {
        var vedtak = medlemskap.getVedtak();
        var erOpprinneligVedtak = vedtak.getEropprinneligvedtak();
        return ErOpprinneligVedtakMapper.map(erOpprinneligVedtak);
    }
}
