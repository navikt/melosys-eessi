package no.nav.melosys.eessi.service.sed.mapper.fra_sed;

import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;

public abstract class FraSedA003Mapper implements NyttLovvalgSedMapper<MedlemskapA003> {
    @Override
    public String hentLovvalgsland(MedlemskapA003 medlemskap) {
        return LandkodeMapper.mapTilNavLandkode(medlemskap.getVedtak().getLand());
    }

    @Override
    public String hentLovvalgsbestemmelse(MedlemskapA003 medlemskap) {
        return medlemskap.getRelevantartikkelfor8832004eller9872009();
    }

    @Override
    public Boolean sedErEndring(MedlemskapA003 medlemskap) {
        return "ja".equals(medlemskap.getVedtak().getErendringsvedtak());
    }

    @Override
    public MedlemskapA003 hentMedlemskap(SED sed) {
        return (MedlemskapA003) sed.getMedlemskap();
    }

    @Override
    public boolean erMidlertidigBestemmelse(MedlemskapA003 medlemskap) {
        return "ja".equals(medlemskap.getIsDeterminationProvisional());
    }
}
