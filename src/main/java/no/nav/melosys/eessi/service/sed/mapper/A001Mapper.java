package no.nav.melosys.eessi.service.sed.mapper;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.sed.SedType;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA001;
import no.nav.melosys.eessi.models.sed.nav.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

public class A001Mapper implements SedMapper<MedlemskapA001> {

    @Override
    public MedlemskapA001 getMedlemskap(SedDataDto sedData) throws MappingException {

        final MedlemskapA001 medlemskap = new MedlemskapA001();
        final Lovvalgsperiode lovvalgsperiode = getLovvalgsperiode(sedData);

        medlemskap.setUnntak(hentUnntak(lovvalgsperiode, sedData));                     //
        medlemskap.setVertsland(hentVertsland(sedData));                                // DONE
        medlemskap.setNåværendemedlemskap(hentNåværendeMedlemskap(lovvalgsperiode));    //
        medlemskap.setForespurtmedlemskap(hentForespurtMedlemskap(lovvalgsperiode));    // DONE
        medlemskap.setSøknadsperiode(hentSøknadsperiode(lovvalgsperiode));              // DONE
        medlemskap.setTidligereperiode(hentTidligerePeriode(lovvalgsperiode));          // DONE
        medlemskap.setAnmodning(hentAnmodning(lovvalgsperiode));                        //
        medlemskap.setDatoforrigesøknad(hentDatoForrigeSøknad(lovvalgsperiode));        //

        /*
        Lovvalgsperiode lovvalgsperiode = sedData.getLovvalgsperiode();

        medlemskap.setVedtak(hentVedtak(lovvalgsperiode));

        if (!sedData.getPersonDokument().erEgenAnsatt) {
            medlemskap.setUtsendingsland(hentUtsendingsland(sedData));
        }
        */

        return medlemskap;
    }

    private Unntak hentUnntak(Lovvalgsperiode lovvalgsperiode, SedDataDto sedData) {
/*
        Unntak unntak = new Unntak();

        unntak.setStartdatoansattforsikret(); // TODO

        Grunnlag grunnlag = new Grunnlag();
        grunnlag.setAnnet(null); // Confluence: "Brukes ikke"
        grunnlag.setArtikkel(LovvalgTilEuxMapper.mapMelosysLovvalgTilEux(lovvalgsperiode.getBestemmelse()));
        unntak.setGrunnlag(grunnlag);

        // TODO: Finner ikke denne i MELOSYS
        SpesielleOmstendigheter spesielleOmstendigheter = new SpesielleOmstendigheter();
        spesielleOmstendigheter.setType();
        spesielleOmstendigheter.setBeskrivelseannensituasjon();
        unntak.setSpesielleomstendigheter(); // TODO

        unntak.setStartdatoansattforsikret(); // TODO

        unntak.setBegrunnelse(lovvalgsperiode.getMedlemskapstype()); // TODO: (Confluence: "FastsattLovvalgsland.LovvalgsPeriode.lovvalgsBegrunnelse")

        unntak.setA1grunnlag("16_R"); // TODO: Avklaring

*/
        return null;
    }

    private Vertsland hentVertsland(SedDataDto sedData) throws MappingException {
        Vertsland vertsland = new Vertsland();
        vertsland.setArbeidsgiver(hentArbeidsGiver(sedData.getUtenlandskeVirksomheter()));

        return vertsland;
    }

    // TODO: Er dette alltid Norge?
    private Collection<Land> hentNåværendeMedlemskap(Lovvalgsperiode lovvalgsperiode) {
        // MULIGE LAND
        /*
        lovvalgsperiode.getBehandlingsresultat().getFastsattAvLand();
        lovvalgsperiode.getUnntakFraLovvalgsland();
        lovvalgsperiode.getLovvalgsland(); // -> hentForespurtMedlemskap
        lovvalgsperiode.getBehandlingsresultat().getLovvalgsperioder().stream().map(lp -> lp.getLovvalgsland());
        lovvalgsperiode.getBehandlingsresultat().getLovvalgsperioder().stream().map(lp -> lp.getUnntakFraLovvalgsland());
        //lovvalgsPeriode.getBehandlingsresultat().getVilkaarsresultater().stream().map(v -> v.getBegrunnelseFritekst())
        lovvalgsperiode.getBehandlingsresultat().getFastsattAvLand();
        */

        Land land = new Land();
        if (lovvalgsperiode.getBehandlingsresultat() != null) {
            if (lovvalgsperiode.getBehandlingsresultat().getFastsattAvLand() != null) {
                land.setLandkode(lovvalgsperiode.getBehandlingsresultat().getFastsattAvLand().getLandkode()); // ???
            }
        }

        return Collections.singletonList(land);
    }

    private Collection<Land> hentForespurtMedlemskap(Lovvalgsperiode lovvalgsperiode) {
        Land land = new Land();
        land.setLandkode(lovvalgsperiode.getLandkode());

        return Collections.singletonList(land);
    }

    // TODO: Avklar om vi henter rett felt
    private Fastperiode hentSøknadsperiode(Lovvalgsperiode lovvalgsperiode) {
        Fastperiode periode = new Fastperiode();

        periode.setStartdato(formaterDato(lovvalgsperiode.getFom()));
        periode.setSluttdato(formaterDato(lovvalgsperiode.getTom()));

        return periode;
    }

    // TODO: Avklar om vi henter rett felt
    private Collection<Tidligereperiode> hentTidligerePeriode(Lovvalgsperiode lovvalgsperiode) {
        return lovvalgsperiode.getBehandlingsresultat().getLovvalgsperioder().stream()
            .map(l -> {
                Tidligereperiode tidligereperiode = new Tidligereperiode();
                Fastperiode fastperiode = new Fastperiode();

                fastperiode.setStartdato(formaterDato(l.getFom()));
                fastperiode.setSluttdato(formaterDato(l.getTom()));
                tidligereperiode.setFastperiode(fastperiode);

                return tidligereperiode;
            })
            .collect(Collectors.toList());
    }

    private Anmodning hentAnmodning(Lovvalgsperiode lovvalgsperiode) {
        Anmodning anmodning = new Anmodning();
        anmodning.setErendring("ja"); // TODO: Finn rett verdi

        return anmodning;
    }

    // TODO: Finnes denne i MELOSYS?
    private String hentDatoForrigeSøknad(Lovvalgsperiode lovvalgsperiode) {
        //lovvalgsperiode.getBehandlingsresultat().getVedtaksdato();
        return null;
    }

    private boolean isKorrektLovvalgbestemmelse(Bestemmelse lovvalgBestemmelse) {
        return lovvalgBestemmelse == Bestemmelse.ART_16_1
            || lovvalgBestemmelse == Bestemmelse.ART_16_2;
    }

    @Override
    public SedType getSedType() {
        return SedType.A001;
    }

    private Lovvalgsperiode getLovvalgsperiode(SedDataDto sedData) {
        return Collections.max(sedData.getLovvalgsperioder(), Comparator.comparing(Lovvalgsperiode::getFom));
    }
}
