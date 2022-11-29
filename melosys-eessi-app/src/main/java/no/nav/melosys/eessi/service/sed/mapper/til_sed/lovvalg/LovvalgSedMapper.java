package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.controller.dto.VedtakDto;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.models.sed.nav.Vedtak;
import no.nav.melosys.eessi.service.sed.mapper.til_sed.SedMapper;

/**
 * Felles mapper-interface for alle typer av lovvalgs-SED'er. Mapper NAV-objektet i NAV-SED, som brukes av eux for
 * å plukke ut nødvendig informasjon for en angitt SED
 */
public interface LovvalgSedMapper<T extends Medlemskap> extends SedMapper {

    // Hvis det skulle trenges noen spesifikke endringer av NAV-objektet for enkelte SED'er,
    // bør denne metoden overrides.
    @Override
    default SED mapTilSed(SedDataDto sedData) {
        var sed = SedMapper.super.mapTilSed(sedData);
        sed.setMedlemskap(getMedlemskap(sedData));

        return sed;
    }

    default void setVedtaksdata(Vedtak vedtak, VedtakDto vedtakDto) {
        if (vedtakDto != null && !vedtakDto.isErFørstegangsvedtak()) {
            vedtak.setDatoforrigevedtak(
                    vedtakDto.getDatoForrigeVedtak() != null ? vedtakDto.getDatoForrigeVedtak().toString() : null
            );
        } else {
            vedtak.setEropprinneligvedtak("ja");
            vedtak.setErendringsvedtak("nei");
        }
    }

    T getMedlemskap(SedDataDto sedData);

}
