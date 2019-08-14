package no.nav.melosys.eessi.service.sed.mapper.lovvalg;

import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003;
import no.nav.melosys.eessi.models.sed.nav.PeriodeA010;
import no.nav.melosys.eessi.models.sed.nav.VedtakA003;

public class A003Mapper implements LovvalgSedMapper<MedlemskapA003> {


    @Override
    public MedlemskapA003 getMedlemskap(SedDataDto sedData) throws MappingException {

        MedlemskapA003 medlemskap = new MedlemskapA003();

        if (!sedData.getLovvalgsperioder().isEmpty()) {
            medlemskap.setVedtak(getVedtak(sedData));
            medlemskap.setRelevantartikkelfor8832004eller9872009(sedData.getLovvalgsperioder().get(0).getBestemmelse().getValue());
        }

        return medlemskap;
    }

    private VedtakA003 getVedtak(SedDataDto sedData) throws MappingException {

        Lovvalgsperiode lovvalgsperiode = sedData.getLovvalgsperioder().stream().findFirst()
                .orElseThrow(() -> new MappingException("Finner ingen lovvalgsperiode"));

        VedtakA003 vedtak = new VedtakA003();
        vedtak.setLand(lovvalgsperiode.getLovvalgsland());
        vedtak.setGjelderperiode(getPeriode(lovvalgsperiode));
        vedtak.setGjeldervarighetyrkesaktivitet("ja");
        vedtak.setEropprinneligvedtak("ja");

        return vedtak;
    }

    private PeriodeA010 getPeriode(Lovvalgsperiode lovvalgsperiode) {
        PeriodeA010 periode = new PeriodeA010();
        periode.setStartdato(formaterDato(lovvalgsperiode.getFom()));
        periode.setSluttdato(formaterDato(lovvalgsperiode.getTom()));
        return periode;
    }

    @Override
    public SedType getSedType() {
        return SedType.A003;
    }
}
