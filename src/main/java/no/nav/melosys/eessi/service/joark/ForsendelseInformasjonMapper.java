package no.nav.melosys.eessi.service.joark;

import java.util.Collections;
import java.util.Optional;
import no.nav.dokarkivsed.api.v1.*;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import org.springframework.util.StringUtils;

class ForsendelseInformasjonMapper {

    private static final String GOSYS_ARKIVSAKSYSTEM = "FS22";

    static ForsendelsesInformasjon createForsendelse(String aktoerId, SedHendelse sedHendelse, Sak sak,
            ParticipantInfo mottaker) {

        return ForsendelsesInformasjon.builder()
                .arkivSak(sak != null ?
                        ArkivSak.builder()
                                .arkivSakId(sak.getId())
                                .arkivSakSystem(GOSYS_ARKIVSAKSYSTEM)
                                .build() : null)
                .mottaker(Organisasjon.builder()
                        .navn(mottaker != null ?
                                mottaker.getName()
                                : "Ikke tilgjengelig")
                        .orgnummer(mottaker != null ?
                                mottaker.getId()
                                : "Ikke tilgjengelig")
                        .build())
                .bruker(StringUtils.isEmpty(aktoerId) ?
                        null : Person.builder().aktoerId(aktoerId).build())
                .bucId(sedHendelse.getRinaSakId())
                .kanalreferanseId(sedHendelse.getSedId())
                .tema(Optional.ofNullable(sak).map(Sak::getTema)
                        .orElse("MED"))
                .build();
    }

    static DokumentInfoHoveddokument hoveddokument(String sedType, byte[] pdf) {
        return DokumentInfoHoveddokument.builder()
                .sedType(sedType)
                .filinfoListe(Collections.singletonList(Filinfo.builder()
                        .dokument(pdf)
                        .variantFormat(VariantFormat.ARKIV)
                        .arkivFilType(ArkivFilType.PDFA)
                        .build()))
                .build();
    }
}