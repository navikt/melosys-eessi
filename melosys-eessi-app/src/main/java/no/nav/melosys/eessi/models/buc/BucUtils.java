package no.nav.melosys.eessi.models.buc;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import no.nav.melosys.eessi.controller.dto.SedStatus;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.bucinfo.BucInfo;

@UtilityClass
public class BucUtils {

    private static final List<String> lovvalgSedTyper = Stream.of(SedType.values()).map(SedType::name)
            .filter(s -> s.startsWith("A")).collect(Collectors.toList());

    public static final Predicate<Document> dokumentErX001Predicate = dokument ->
            SedType.X001.name().equals(dokument.getType());

    public static final Predicate<Document> dokumentErOpprettet = dokument ->
            !SedStatus.TOM.name().equalsIgnoreCase(dokument.getStatus());

    public static final Predicate<BUC> bucKanLukkesPredicate = buc ->
            buc.getActions().stream().anyMatch(action -> SedType.X001.name().equals(action.getDocumentType()));

    public static final Predicate<BucInfo> norgeErCaseOwnerPredicate = bucInfo ->
            "PO".equalsIgnoreCase(bucInfo.getApplicationRoleId());

    public static final Predicate<Document> sisteSendtLovvalgsSedPredicate = document ->
            !document.getConversations().isEmpty()
            && document.getConversations().get(0).getVersionId() != null
            && lovvalgSedTyper.contains(document.getType());
}
