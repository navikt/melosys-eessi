package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import java.time.LocalDate;
import java.util.Set;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.controller.dto.VedtakDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA010;
import no.nav.melosys.eessi.models.sed.nav.MeldingOmLovvalg;
import no.nav.melosys.eessi.models.sed.nav.PeriodeA010;
import no.nav.melosys.eessi.models.sed.nav.Utsendingsland;
import no.nav.melosys.eessi.models.sed.nav.VedtakA010;

import static no.nav.melosys.eessi.controller.dto.Bestemmelse.*;

public class A010Mapper implements LovvalgSedMapper<MedlemskapA010> {

    private static final Set<Bestemmelse> LOVLIGE_BESTEMMELSER_A010 = Set.of(ART_11_3_b, ART_11_3_c, ART_11_3_d, ART_11_4, ART_11_5, ART_15);

    @Override
    public MedlemskapA010 getMedlemskap(SedDataDto sedData) {
        VedtakDto vedtakDto = sedData.getVedtakDto();
        MedlemskapA010 medlemskap = new MedlemskapA010();

        Lovvalgsperiode lovvalgsperiode = getLovvalgsperiode(sedData);

        if (lovvalgsperiode != null) {
            medlemskap.setMeldingomlovvalg(hentMeldingOmLovvalg(lovvalgsperiode));
            medlemskap.setVedtak(hentVedtak(lovvalgsperiode,vedtakDto));
        }

        medlemskap.setAndreland(getAndreland(sedData));
        return medlemskap;
    }

    private Utsendingsland getAndreland(SedDataDto sedData) {
        final String lovvalgsland = sedData.finnLovvalgslandDefaultNO();
        Utsendingsland utsendingsland = new Utsendingsland();
        utsendingsland.setArbeidsgiver(hentArbeidsgivereIkkeILand(sedData.getArbeidsgivendeVirksomheter(), lovvalgsland));
        return utsendingsland;
    }

    private VedtakA010 hentVedtak(Lovvalgsperiode lovvalgsperiode, VedtakDto vedtakDto) {
        VedtakA010 vedtak = new VedtakA010();
        setOpprinneligVedtak(vedtakDto,vedtak);
        vedtak.setGjelderperiode(hentPeriode(lovvalgsperiode));
        vedtak.setLand(lovvalgsperiode.getLovvalgsland());
        vedtak.setGjeldervarighetyrkesaktivitet("ja");
        return vedtak;
    }

    private void setOpprinneligVedtak(VedtakDto vedtakDto, VedtakA010 vedtakA010)
    {
        if (vedtakDto != null) {
            if (!vedtakDto.isErFoerstegangsVedtak()) {
                vedtakA010.setEropprinneligvedtak("nei");
                vedtakA010.setDatoforrigevedtak(vedtakDto.getDatoForrigePeriode().toString());
            }
        }else{
            vedtakA010.setEropprinneligvedtak("ja");
            vedtakA010.setDatoforrigevedtak(null);
        }
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
