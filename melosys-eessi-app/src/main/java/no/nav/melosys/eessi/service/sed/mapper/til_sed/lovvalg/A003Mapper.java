package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003;
import no.nav.melosys.eessi.models.sed.nav.Andreland;
import no.nav.melosys.eessi.models.sed.nav.PeriodeA010;
import no.nav.melosys.eessi.models.sed.nav.VedtakA003;

import java.util.Optional;

public class A003Mapper implements LovvalgSedMapper<MedlemskapA003> {

    @Override
    public MedlemskapA003 getMedlemskap(SedDataDto sedData) {

        MedlemskapA003 medlemskap = new MedlemskapA003();

        medlemskap.setVedtak(getVedtak(sedData));
        medlemskap.setAndreland(getAndreLand(sedData));

        if (!sedData.getLovvalgsperioder().isEmpty()) {
            medlemskap.setRelevantartikkelfor8832004eller9872009(sedData.getLovvalgsperioder().get(0).getBestemmelse().getValue());
        }

        return medlemskap;
    }

    private VedtakA003 getVedtak(SedDataDto sedData) {
        VedtakA003 vedtak = new VedtakA003();
        final Optional<Lovvalgsperiode> lovvalgsperiode = sedData.finnLovvalgsperiode();

        if (lovvalgsperiode.isPresent()) {
            vedtak.setLand(lovvalgsperiode.get().getLovvalgsland());
            vedtak.setGjelderperiode(getPeriode(lovvalgsperiode.get()));
        }

        vedtak.setGjeldervarighetyrkesaktivitet("ja");
        setVedtaksdata(vedtak,sedData.getVedtakDto());

        return vedtak;
    }

    private PeriodeA010 getPeriode(Lovvalgsperiode lovvalgsperiode) {
        PeriodeA010 periode = new PeriodeA010();
        periode.setStartdato(formaterDato(lovvalgsperiode.getFom()));
        periode.setSluttdato(formaterDato(lovvalgsperiode.getTom()));
        return periode;
    }


    private Andreland getAndreLand(SedDataDto sedData) {
        final String lovvalgsland = sedData.finnLovvalgslandDefaultNO();
        Andreland andreland = new Andreland();
        andreland.setArbeidsgiver(hentArbeidsgivereIkkeILand(sedData.getArbeidsgivendeVirksomheter(), lovvalgsland));
        return andreland;
    }

    @Override
    public SedType getSedType() {
        return SedType.A003;
    }
}
