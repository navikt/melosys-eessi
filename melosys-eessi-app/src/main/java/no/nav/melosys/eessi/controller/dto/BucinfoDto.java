package no.nav.melosys.eessi.controller.dto;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Document;
import no.nav.melosys.eessi.models.buc.Organisation;
import no.nav.melosys.eessi.models.buc.Participant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BucinfoDto {

    private String id;
    private boolean erÅpen;
    private String bucType;
    private Long opprettetDato;
    private Set<String> mottakerinstitusjoner;
    private List<SedinfoDto> seder;

    public static BucinfoDto av(BUC buc, List<String> statuser, String rinaUrlPrefix) {
        return BucinfoDto.builder()
                .id(buc.getId())
                .erÅpen(buc.erÅpen())
                .bucType(buc.getBucType())
                .opprettetDato(buc.getStartDate().toInstant().toEpochMilli())
                .mottakerinstitusjoner(hentMottakerinstitusjonerFraBuc(buc))
                .seder(buc.getDocuments().stream()
                        .filter(filtrerMedStatus(statuser))
                        .map(doc -> SedinfoDto.av(doc, buc.getId(), rinaUrlPrefix))
                        .collect(Collectors.toList()))
                .build();
    }

    private static Predicate<Document> filtrerMedStatus(List<String> statuser) {
        if (statuser == null || statuser.isEmpty()) {
            return b -> true;
        }

        return b -> statuser.stream().map(SedStatus::fraNorskStatus)
                .anyMatch(status -> status == SedStatus.fraEngelskStatus(b.getStatus()));
    }

    private static Set<String> hentMottakerinstitusjonerFraBuc(BUC buc) {
        return buc.getDocuments().stream()
                .flatMap(document -> document.getConversations().stream()
                        .flatMap(conversation -> conversation.getParticipants().stream()))
                .filter(participant -> participant.getRole() == Participant.ParticipantRole.MOTTAKER)
                .map(Participant::getOrganisation)
                .map(Organisation::getId).collect(Collectors.toSet());
    }
}
