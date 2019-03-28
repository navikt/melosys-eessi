package no.nav.melosys.eessi.service.joark;

import java.time.Instant;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import no.nav.dok.tjenester.mottainngaaendeforsendelse.*;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.service.dokkat.DokkatSedInfo;

class InngaaendeForsendelseMapper {

    private static final String MOTTAKS_KANAL = "EESSI";
    private static final String AVSENDER_IKKE_TILGJENGELIG = "avsender ikke tilgjengelig";

    static MottaInngaaendeForsendelseRequest createMottaInngaaendeForsendelseRequest(
            String aktoerId, SedHendelse sedMottatt, Sak sak, DokkatSedInfo dokkatSedInfo, ParticipantInfo senderInfo, byte[] pdf) {

        return new MottaInngaaendeForsendelseRequest()
                .withForsokEndeligJF(Boolean.FALSE)
                .withForsendelseInformasjon(forsendelseInformasjon(aktoerId, sedMottatt, sak, dokkatSedInfo, senderInfo))
                .withDokumentInfoHoveddokument(hoveddokument(dokkatSedInfo, pdf));
    }

    private static ForsendelseInformasjon forsendelseInformasjon(
            String aktoerId, SedHendelse sedMottatt, Sak sak, DokkatSedInfo dokkatSedInfo, ParticipantInfo senderInfo) {

        return new ForsendelseInformasjon()
                .withBruker(person(aktoerId))
                .withAvsender(organisasjon(senderInfo.getId(), senderInfo.getName()))
                .withTema(sak.getTema())
                .withKanalReferanseId(sedMottatt.getSedId())
                .withForsendelseMottatt(Date.from(Instant.now()))
                .withForsendelseInnsendt(Date.from(Instant.now()))
                .withMottaksKanal(MOTTAKS_KANAL)
                .withTittel(dokkatSedInfo.getDokumentTittel());
    }

    private static DokumentInfoHoveddokument hoveddokument(DokkatSedInfo dokkatSedInfo, byte[] pdf) {
        return new DokumentInfoHoveddokument()
                .withDokumentTypeId(dokkatSedInfo.getDokumenttypeId())
                .withDokumentVariant(Collections.singletonList(new DokumentVariant()
                        .withArkivFilType(DokumentVariant.ArkivFilType.PDFA)
                        .withVariantFormat(DokumentVariant.VariantFormat.ARKIV)
                        .withDokument(pdf)));
    }

    static Aktoer person(String ident) {
        return ident != null ?
                new Aktoer().withAdditionalProperty("aktoer",
                        KeyValue.of("person",
                                KeyValue.of("ident", ident)))
                : null;
    }

    static Aktoer organisasjon(String orgnr, String navn) {
        return orgnr != null ?
                new Aktoer().withAdditionalProperty("aktoer",
                        KeyValue.of("organisasjon",
                                collect(KeyValue.of("orgnr", orgnr),
                                        KeyValue.of("navn", navn))))

                //dokmotinngående støtter ikke null i avsender så returner default verdier
                : new Aktoer().withAdditionalProperty("aktoer",
                KeyValue.of("organisasjon",
                        collect(KeyValue.of("orgnr", AVSENDER_IKKE_TILGJENGELIG),
                                KeyValue.of("navn", AVSENDER_IKKE_TILGJENGELIG))));
    }

    @SafeVarargs
    private static <T, U> Map<T, U> collect(Map.Entry<T, U>... entries) {
        return Stream.of(entries).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static final class KeyValue {
        static <T, U> Map.Entry<T, U> of(T key, U value) {
            return new AbstractMap.SimpleImmutableEntry<>(key, value);
        }
    }
}
