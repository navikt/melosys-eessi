package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.controller.dto.UtpekingAvvisDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA004;
import no.nav.melosys.eessi.models.sed.nav.Avslag;
import no.nav.melosys.eessi.models.sed.nav.Land;

public class A004Mapper implements LovvalgSedMapper<MedlemskapA004> {

    @Override
    public MedlemskapA004 getMedlemskap(SedDataDto sedData) {
        UtpekingAvvisDto utpekingAvvis = sedData.getUtpekingAvvis();

        if (utpekingAvvis == null) {
            throw new MappingException("Trenger UtpekingAvvis for å opprette A004");
        }

        MedlemskapA004 medlemskap = new MedlemskapA004();
        medlemskap.setAvslag(getAvslag(
            utpekingAvvis.isVilSendeAnmodningOmMerInformasjon(),
            utpekingAvvis.getNyttLovvalgsland(),
            utpekingAvvis.getBegrunnelseUtenlandskMyndighet()
        ));

        return medlemskap;
    }

    private Avslag getAvslag(boolean vilSendeAnmodningOmMerInformasjon, String nyttLovvalgsland, String begrunnelseUtenlandskMyndighet) {
        Avslag avslag = new Avslag();
        avslag.setErbehovformerinformasjon(vilSendeAnmodningOmMerInformasjon ? "ja" : "nei");
        if (nyttLovvalgsland != null) {
            Land lovvalgsland = new Land();
            lovvalgsland.setLandkode(nyttLovvalgsland);
            avslag.setForslagformedlemskap(lovvalgsland);
        }
        avslag.setBegrunnelse(begrunnelseUtenlandskMyndighet);
        return avslag;
    }

    @Override
    public SedType getSedType() {
        return SedType.A004;
    }
}
