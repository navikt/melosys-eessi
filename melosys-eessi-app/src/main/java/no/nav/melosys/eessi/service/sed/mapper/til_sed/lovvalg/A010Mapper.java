package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import java.util.Optional;
import java.util.Set;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.*;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA010;
import no.nav.melosys.eessi.models.sed.nav.*;

import static no.nav.melosys.eessi.controller.dto.Bestemmelse.*;

public class A010Mapper implements LovvalgSedMapper<MedlemskapA010> {

    private static final Set<Bestemmelse> LOVLIGE_BESTEMMELSER_A010 = Set.of(ART_11_3_b, ART_11_3_c, ART_11_3_d, ART_11_4, ART_11_5, ART_15);

    @Override
    public MedlemskapA010 getMedlemskap(SedDataDto sedData) {
        MedlemskapA010 medlemskap = new MedlemskapA010();
        Optional<Lovvalgsperiode> lovvalgsperiode = sedData.finnLovvalgsperiode();

        lovvalgsperiode.ifPresent(value -> medlemskap.setMeldingomlovvalg(hentMeldingOmLovvalg(value)));

        medlemskap.setVedtak(hentVedtak(sedData));
        medlemskap.setAndreland(getAndreland(sedData));
        return medlemskap;
    }

    private VedtakA010 hentVedtak(SedDataDto sedDataDto){
        VedtakA010 vedtak = new VedtakA010();
        final Optional<Lovvalgsperiode> lovvalgsperiode = sedDataDto.finnLovvalgsperiode();
        if (lovvalgsperiode.isPresent()){
            vedtak.setGjelderperiode(hentPeriode(lovvalgsperiode.get()));
            vedtak.setLand(lovvalgsperiode.get().getLovvalgsland());
        }

        setVedtaksdata(vedtak, sedDataDto.getVedtakDto());
        vedtak.setGjeldervarighetyrkesaktivitet("ja");
        return vedtak;
    }

    private Utsendingsland getAndreland(SedDataDto sedData) {
        final String lovvalgsland = sedData.finnLovvalgslandDefaultNO();
        Utsendingsland utsendingsland = new Utsendingsland();
        utsendingsland.setArbeidsgiver(hentArbeidsgivereIkkeILand(sedData.getArbeidsgivendeVirksomheter(), lovvalgsland));
        return utsendingsland;
    }

    private MeldingOmLovvalg hentMeldingOmLovvalg(Lovvalgsperiode lovvalgsperiode) {
        MeldingOmLovvalg meldingOmLovvalg = new MeldingOmLovvalg();
        meldingOmLovvalg.setArtikkel(tilA010Bestemmelse(lovvalgsperiode));
        return  meldingOmLovvalg;
    }

    private String tilA010Bestemmelse(Lovvalgsperiode lovvalgsperiode) {

        if (LOVLIGE_BESTEMMELSER_A010.contains(lovvalgsperiode.getBestemmelse())) {
            return lovvalgsperiode.getBestemmelse().getValue();
        } else if (lovvalgsperiode.harTilleggsbestemmelse() && LOVLIGE_BESTEMMELSER_A010.contains(lovvalgsperiode.getTilleggsBestemmelse())) {
            return lovvalgsperiode.getTilleggsBestemmelse().getValue();
        }

        throw new MappingException("Kan ikke mappe til bestemmelse i A010 for lovvalgsperiode {}");
    }

    private PeriodeA010 hentPeriode(Lovvalgsperiode lovvalgsperiode) {
        PeriodeA010 periode = new PeriodeA010();
        periode.setStartdato(formaterDato(lovvalgsperiode.getFom()));
        periode.setSluttdato(formaterDato(lovvalgsperiode.getTom()));
        return periode;
    }

    @Override
    public SedType getSedType() {
        return SedType.A010;
    }
}
