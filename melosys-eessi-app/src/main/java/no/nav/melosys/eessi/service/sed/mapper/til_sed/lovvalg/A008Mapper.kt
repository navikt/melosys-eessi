package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import java.util.Optional;

import no.nav.melosys.eessi.controller.dto.Periode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA008;
import no.nav.melosys.eessi.models.sed.nav.ArbeidIFlereLand;
import no.nav.melosys.eessi.models.sed.nav.Bosted;
import no.nav.melosys.eessi.models.sed.nav.MedlemskapA008Bruker;
import no.nav.melosys.eessi.models.sed.nav.Yrkesaktivitet;

public class A008Mapper implements LovvalgSedMapper<MedlemskapA008> {

    @Override
    public MedlemskapA008 getMedlemskap(SedDataDto sedData) {
        return new MedlemskapA008(null, hentA008Bruker(sedData));
    }

    private MedlemskapA008Bruker hentA008Bruker(SedDataDto sedData) {
        var bruker = new MedlemskapA008Bruker();
        var arbeidIFlereLand = new ArbeidIFlereLand();
        arbeidIFlereLand.setBosted(new Bosted(sedData.getAvklartBostedsland()));

        Optional.ofNullable(sedData.getSøknadsperiode())
            .map(Periode::getFom)
            .ifPresent(søknadsperiodeFom -> {
                var yrkesaktivitet = new Yrkesaktivitet();
                yrkesaktivitet.setStartdato(formaterDato(søknadsperiodeFom));
                arbeidIFlereLand.setYrkesaktivitet(yrkesaktivitet);
            });

        bruker.setArbeidiflereland(arbeidIFlereLand);
        return bruker;
    }

    @Override
    public SedType getSedType() {
        return SedType.A008;
    }
}
