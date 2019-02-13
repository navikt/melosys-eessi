package no.nav.melosys.eessi.service.sed.mapper;

import java.util.Collection;
import java.util.Collections;

import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SedType;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA001;
import no.nav.melosys.eessi.models.sed.nav.*;
import no.nav.melosys.eessi.service.sed.helpers.A1GrunnlagMapper;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;

public class A001Mapper implements SedMapper<MedlemskapA001> {

    @Override
    public MedlemskapA001 getMedlemskap(SedDataDto sedData) throws MappingException, NotFoundException {

        final MedlemskapA001 medlemskap = new MedlemskapA001();
        final Lovvalgsperiode lovvalgsperiode = getLovvalgsperiode(sedData);

        medlemskap.setUnntak(getUnntak(lovvalgsperiode));
        medlemskap.setVertsland(getVertsland(sedData));
        medlemskap.setNåværendemedlemskap(getNåværendeMedlemskap(sedData));
        medlemskap.setForespurtmedlemskap(getForespurtMedlemskap(lovvalgsperiode));
        medlemskap.setSøknadsperiode(getSøknadsperiode(lovvalgsperiode));
        medlemskap.setTidligereperiode(getTidligerePeriode());
        medlemskap.setAnmodning(getAnmodning());

        return medlemskap;
    }

    // Unntak inneholder for det meste felt i 10.x
    // Feltene i 10.x blir ikke tatt med, bortsett fra a1Grunnlag og begrunnelse
    private Unntak getUnntak(Lovvalgsperiode lovvalgsperiode) throws MappingException {
        Unntak unntak = new Unntak();

        // Hent fast tekst (samme som i brev), denne kan overskrives av saksbehandler (særlig grunn)
        // Verdien blir satt inn i en tekstboks i RINA. Viser fritekst dersom den har en verdi, og eventuelle begrunnelser.
        unntak.setBegrunnelse(lovvalgsperiode.getBegrunnelse());

        // Mapper verdi fra getUnntakFraBestemmelse() til format som eux vil motta.
        unntak.setA1grunnlag(A1GrunnlagMapper.mapFromBestemmelse(lovvalgsperiode.getUnntakFraBestemmelse()));

        return unntak;
    }

    private Vertsland getVertsland(SedDataDto sedData) throws MappingException, NotFoundException {
        Vertsland vertsland = new Vertsland();
        vertsland.setArbeidsgiver(hentArbeidsGiver(sedData.getUtenlandskeVirksomheter()));

        return vertsland;
    }

    private Collection<Land> getNåværendeMedlemskap(SedDataDto sedDataDto) throws NotFoundException {
        // Person sitt statsborgerskap
        Land land = new Land();
        if (sedDataDto.getBruker() != null) {
            land.setLandkode(LandkodeMapper.getLandkodeIso2(sedDataDto.getBruker().getStatsborgerskap()));
        }

        return Collections.singletonList(land);
    }

    private Collection<Land> getForespurtMedlemskap(Lovvalgsperiode lovvalgsperiode) throws NotFoundException {
        Land land = new Land();
        land.setLandkode(LandkodeMapper.getLandkodeIso2(lovvalgsperiode.getLandkode()));

        return Collections.singletonList(land);
    }

    private Fastperiode getSøknadsperiode(Lovvalgsperiode lovvalgsperiode) {
        Fastperiode periode = new Fastperiode();

        periode.setStartdato(formaterDato(lovvalgsperiode.getFom()));
        periode.setSluttdato(formaterDato(lovvalgsperiode.getTom()));

        return periode;
    }

    // Blir ikke implementert i denne versjonen av Melosys.
    private Collection<Tidligereperiode> getTidligerePeriode() {
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
