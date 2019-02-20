package no.nav.melosys.eessi.service.sed.mapper;

import java.util.Collections;
import java.util.List;
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SedType;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA001;
import no.nav.melosys.eessi.models.sed.nav.*;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;
import no.nav.melosys.eessi.service.sed.helpers.UnntakArtikkelMapper;

public class A001Mapper implements SedMapper<MedlemskapA001> {

    @Override
    public MedlemskapA001 getMedlemskap(SedDataDto sedData) throws MappingException, NotFoundException {

        final MedlemskapA001 medlemskap = new MedlemskapA001();
        final Lovvalgsperiode lovvalgsperiode = getLovvalgsperiode(sedData);

        medlemskap.setUnntak(getUnntak(lovvalgsperiode));
        medlemskap.setVertsland(getVertsland(sedData));
        medlemskap.setNaavaerendemedlemskap(getNaavaerendeMedlemskap(sedData));
        medlemskap.setForespurtmedlemskap(getForespurtMedlemskap(lovvalgsperiode));
        medlemskap.setSoeknadsperiode(getSoeknadsperiode(lovvalgsperiode));
        medlemskap.setTidligereperiode(getTidligerePeriode());
        medlemskap.setAnmodning(getAnmodning());

        return medlemskap;
    }

    private Unntak getUnntak(Lovvalgsperiode lovvalgsperiode) throws MappingException {
        Unntak unntak = new Unntak();

        // Hent fast tekst (samme som i brev), denne kan overskrives av saksbehandler (særlig grunn)
        unntak.setBegrunnelse(lovvalgsperiode.getUnntaksBegrunnelse());

        Grunnlag grunnlag = new Grunnlag();
        grunnlag.setArtikkel(UnntakArtikkelMapper.mapFromBestemmelse(lovvalgsperiode.getUnntakFraBestemmelse()));

        if (UnntakArtikkelMapper.BESTEMMELSE_OTHER.equals(grunnlag.getAnnet())) {
            // Støttes ikke i denne versjonen av melosys
            grunnlag.setAnnet(""); // maks 255 tegn
        }
        unntak.setGrunnlag(grunnlag);

        //unntak.setA1grunnlag(A1GrunnlagMapper.mapFromBestemmelse(tidligereLovvalgsperiode.getUnntakFraBestemmelse()));

        return unntak;
    }

    private Vertsland getVertsland(SedDataDto sedData) throws MappingException, NotFoundException {
        Vertsland vertsland = new Vertsland();
        vertsland.setArbeidsgiver(hentArbeidsGiver(sedData.getUtenlandskeVirksomheter()));

        return vertsland;
    }

    private List<Land> getNaavaerendeMedlemskap(SedDataDto sedDataDto) throws NotFoundException {
        // Person sitt statsborgerskap
        Land land = new Land();
        if (sedDataDto.getBruker() != null) {
            land.setLandkode(LandkodeMapper.getLandkodeIso2(sedDataDto.getBruker().getStatsborgerskap()));
        }

        return Collections.singletonList(land);
    }

    private List<Land> getForespurtMedlemskap(Lovvalgsperiode lovvalgsperiode) throws NotFoundException {
        Land land = new Land();
        land.setLandkode(LandkodeMapper.getLandkodeIso2(lovvalgsperiode.getLandkode()));

        return Collections.singletonList(land);
    }

    private Fastperiode getSoeknadsperiode(Lovvalgsperiode lovvalgsperiode) {
        Fastperiode periode = new Fastperiode();

        periode.setStartdato(formaterDato(lovvalgsperiode.getFom()));
        periode.setSluttdato(formaterDato(lovvalgsperiode.getTom()));

        return periode;
    }

    // Blir ikke implementert i denne versjonen av Melosys.
    private List<Periode> getTidligerePeriode() {
        return null;
    }

    // Blir ikke implementert i denne versjonen av Melosys.
    private Anmodning getAnmodning() {
        Anmodning anmodning = new Anmodning();
        anmodning.setErendring("nei"); // Hardkodes til "nei" inntil videre

        return anmodning;
    }

    @Override
    public SedType getSedType() {
        return SedType.A001;
    }
}
