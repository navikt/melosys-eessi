package no.nav.melosys.eessi.service.joark;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.fasterxml.jackson.databind.JsonNode;
import no.nav.dok.tjenester.mottainngaaendeforsendelse.Aktoer;

class JournalpostUtils {

    private static final String AVSENDER_IKKE_TILGJENGELIG = "avsender ikke tilgjengelig";

    static ParticipantInfo extractReceiverInformation(JsonNode participants) {
        return extractParticipantInformation(participants, "CounterParty");
    }

    static ParticipantInfo extractSenderInformation(JsonNode participants) {
        return extractParticipantInformation(participants, "CaseOwner");
    }

    private static ParticipantInfo extractParticipantInformation(JsonNode participants, String participantRole) {
        if (participants.isArray()) {
            for (JsonNode deltager : participants) {
                if (participantRole.equalsIgnoreCase(deltager.get("role").asText())) {
                    JsonNode organization = deltager.get("organisation");

                    ParticipantInfo participantInfo = new ParticipantInfo();
                    participantInfo.setId(organization.get("id").textValue());
                    participantInfo.setName(organization.get("name").textValue());
                    return participantInfo;
                }
            }
        }

        return null;
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
