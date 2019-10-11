package no.nav.melosys.eessi.controller.dto;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public enum SedStatus {
    UTKAST("UTKAST", "NEW"),
    SENDT("SENDT", "SENT"),
    MOTTATT("MOTTATT", "RECEIVED"),
    TOM("TOM", "EMPTY"),
    AVBRUTT("AVBRUTT", "CANCELLED");

    private final String norskStatus;

    private final String engelskStatus;

    static Collection<SedStatus> gyldigOpprettetStatuser = EnumSet.of(SENDT, MOTTATT);

    SedStatus(String norskStatus, String engelskStatus) {
        this.engelskStatus = engelskStatus;
        this.norskStatus = norskStatus;
    }

    public static SedStatus fraNorskStatus(String norskStatus) {

        if (StringUtils.isEmpty(norskStatus)) {
            return null;
        }

        return valueOf(norskStatus.toUpperCase());
    }

    public static SedStatus fraEngelskStatus(String engelskStatus) {

        if (StringUtils.isEmpty(engelskStatus)) {
            return null;
        }

        return Arrays.stream(values())
                .filter(status -> engelskStatus.equalsIgnoreCase(status.engelskStatus))
                .findFirst()
                .orElse(null);
    }

    public static boolean erGyldigEngelskStatus(String engelskstatus) {
        return MOTTATT.engelskStatus.equalsIgnoreCase(engelskstatus)
                || SENDT.engelskStatus.equalsIgnoreCase(engelskstatus);
    }
}
