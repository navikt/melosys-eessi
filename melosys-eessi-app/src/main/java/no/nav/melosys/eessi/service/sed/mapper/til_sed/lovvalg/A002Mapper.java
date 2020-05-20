package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import java.time.LocalDate;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.controller.dto.SvarAnmodningUnntakDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA002;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.SvarAnmodningUnntakBeslutning;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.UnntakA002;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.VedtakA002;
import no.nav.melosys.eessi.models.sed.nav.Fastperiode;
import no.nav.melosys.eessi.models.sed.nav.Periode;

public class A002Mapper implements LovvalgSedMapper<MedlemskapA002> {

    @Override
    public MedlemskapA002 getMedlemskap(SedDataDto sedData) {
        SvarAnmodningUnntakDto svarAnmodningUnntak = sedData.getSvarAnmodningUnntak();

        if (svarAnmodningUnntak == null) {
            throw new MappingException("Trenger SvarAnmodningUnntak for å opprette A002");
        }

        MedlemskapA002 medlemskapA002 = new MedlemskapA002();
        medlemskapA002.setUnntak(getUnntak(
                svarAnmodningUnntak.getBegrunnelse(),
                svarAnmodningUnntak.getBeslutning(),
                svarAnmodningUnntak.getDelvisInnvilgetPeriode().getFom(),
                svarAnmodningUnntak.getDelvisInnvilgetPeriode().getTom()
        ));

        return medlemskapA002;
    }

    private UnntakA002 getUnntak(String begrunnelse, SvarAnmodningUnntakBeslutning resultat, LocalDate delvisInnvilgetFom, LocalDate delvisInnvilgetTom) {
        UnntakA002 unntak = new UnntakA002();
        unntak.setVedtak(getVedtak(begrunnelse, resultat, delvisInnvilgetFom, delvisInnvilgetTom));
        return unntak;
    }

    private VedtakA002 getVedtak(String begrunnelse, SvarAnmodningUnntakBeslutning resultat, LocalDate delvisInnvilgetFom, LocalDate delvisInnvilgetTom) {
        VedtakA002 vedtak = new VedtakA002();
        vedtak.setAnnenperiode(getPeriode(delvisInnvilgetFom, delvisInnvilgetTom));
        vedtak.setBegrunnelse(begrunnelse);
        vedtak.setResultat(resultat.getRinaKode());
        return vedtak;
    }

    private Periode getPeriode(LocalDate delvisInnvilgetFom, LocalDate delvisInnvilgetTom) {
        Periode periode = new Periode();
        periode.setFastperiode(getFastperiode(delvisInnvilgetFom, delvisInnvilgetTom));
        return periode;
    }

    private Fastperiode getFastperiode(LocalDate delvisInnvilgetFom, LocalDate delvisInnvilgetTom) {
        Fastperiode fastperiode = new Fastperiode();
        fastperiode.setStartdato(formaterDatoEllerNull(delvisInnvilgetFom));
        fastperiode.setSluttdato(formaterDatoEllerNull(delvisInnvilgetTom));
        return fastperiode;
    }

    private String formaterDatoEllerNull(LocalDate dato) {
        if (dato == null) {
            return null;
        }
        return formaterDato(dato);
    }

    @Override
    public SedType getSedType() {
        return SedType.A002;
    }
}
