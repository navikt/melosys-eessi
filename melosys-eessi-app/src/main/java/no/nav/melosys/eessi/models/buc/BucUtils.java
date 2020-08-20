package no.nav.melosys.eessi.models.buc;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.SedStatus;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.bucinfo.BucInfo;
import no.nav.melosys.eessi.models.sed.SED;

import static no.nav.melosys.eessi.models.sed.Konstanter.DEFAULT_SED_G_VER;
import static no.nav.melosys.eessi.models.sed.Konstanter.DEFAULT_SED_VER;

@Slf4j
@UtilityClass
public class BucUtils {

    private static final List<String> lovvalgSedTyper = Stream.of(SedType.values()).map(SedType::name)
            .filter(s -> s.startsWith("A")).collect(Collectors.toList());

    public static final Predicate<Document> dokumentErX001Predicate = dokument ->
            SedType.X001.name().equals(dokument.getType());

    public static final Predicate<Document> dokumentErOpprettet = dokument ->
            !SedStatus.TOM.getEngelskStatus().equalsIgnoreCase(dokument.getStatus());

    public static final Predicate<BUC> bucKanLukkesPredicate = BucUtils::bucKanLukkes;

    public static final Predicate<BucInfo> norgeErCaseOwner = bucInfo ->
            "PO".equalsIgnoreCase(bucInfo.getApplicationRoleId());

    public static final Predicate<BucInfo> bucErÅpen = bucInfo ->
            "open".equalsIgnoreCase(bucInfo.getStatus());

    public static final Predicate<Document> sisteSendtLovvalgsSedPredicate = document ->
            !document.getConversations().isEmpty()
            && document.getConversations().get(0).getVersionId() != null
            && lovvalgSedTyper.contains(document.getType());

    private static final Pattern VERSJON_PATTERN = Pattern.compile("^v(\\d)\\.(\\d)$");

    public static void verifiserSedVersjonErBucVersjon(BUC buc, SED sed) {
        final String ønsketSedVersjon = String.format("v%s.%s", sed.getSedGVer(), sed.getSedVer());

        if (!ønsketSedVersjon.equalsIgnoreCase(buc.getBucVersjon())) {
            log.info("Rina-sak {} er på gammel versjon {}. Oppdaterer SED til å bruke gammel versjon", buc.getId(), buc.getBucVersjon());
            sed.setSedGVer(BucUtils.parseGVer(buc));
            sed.setSedVer(BucUtils.parseVer(buc));
        }
    }

    public static boolean bucKanLukkes(BUC buc) {
        BucType bucType = BucType.valueOf(buc.getBucType());

        if (bucType == BucType.LA_BUC_06) {
            return buc.harMottattSedTypeAntallDagerSiden(SedType.A006, 30)
                    && kanOppretteX001(buc);
        }

        return kanOppretteX001(buc);
    }

    private static boolean kanOppretteX001(BUC buc) {
        return buc.getActions().stream().anyMatch(action -> SedType.X001.name().equals(action.getDocumentType()));
    }

    String parseGVer(BUC buc) {
        Matcher matcher = VERSJON_PATTERN.matcher(buc.getBucVersjon());
        return matcher.find() ? matcher.group(1) : DEFAULT_SED_G_VER;
    }

    String parseVer(BUC buc) {
        Matcher matcher = VERSJON_PATTERN.matcher(buc.getBucVersjon());
        return matcher.find() ? matcher.group(2) : DEFAULT_SED_VER;
    }

}
