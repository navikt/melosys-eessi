package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA008;
import no.nav.melosys.eessi.models.sed.nav.*;
import org.springframework.util.StringUtils;

public class A008Mapper implements LovvalgSedMapper<MedlemskapA008> {

    @Override
    public MedlemskapA008 getMedlemskap(SedDataDto sedData) {
        MedlemskapA008 medlemskap = new MedlemskapA008();
        if (!StringUtils.isEmpty(sedData.getAvklartBostedsland())) {
            //For videresending av søknad - fyller ut arbeidsland, ikke påkrevd
            medlemskap.setBruker(hentA008Bruker(sedData));
        }

        medlemskap.setEndring(hentEndringA008(sedData));

        return medlemskap;
    }

    private MedlemskapA008Bruker hentA008Bruker(SedDataDto sedData) {
        MedlemskapA008Bruker bruker = new MedlemskapA008Bruker();

        Bosted bosted = new Bosted();
        bosted.setLand(sedData.getAvklartBostedsland());

        Yrkesaktivitet yrkesaktivitet = new Yrkesaktivitet();
        yrkesaktivitet.setStartdato(
                sedData.getLovvalgsperioder().stream().findFirst()
                        .map(Lovvalgsperiode::getFom)
                        .map(this::formaterDato)
                        .orElse(null)
        );

        ArbeidIFlereLand arbeidIFlereLand = new ArbeidIFlereLand();
        arbeidIFlereLand.setBosted(bosted);
        arbeidIFlereLand.setYrkesaktivitet(yrkesaktivitet);

        bruker.setArbeidiflereland(arbeidIFlereLand);
        return bruker;
    }

    private EndringA008 hentEndringA008(SedDataDto sedData) {
        EndringA008 endring = new EndringA008();
        endring.setAdresse(hentAdresseFraDtoAdresse(sedData.getBostedsadresse()));
        return endring;
    }

    @Override
    public SedType getSedType() {
        return SedType.A008;
    }
}
