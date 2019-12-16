package no.nav.melosys.eessi.controller.dto;

import java.util.Arrays;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public enum SedStatus {
    SENDT("SENDT", "SENT"),
    UTKAST("UTKAST", "NEW"),
    TOM("TOM", "EMPTY"),
    MOTTATT("MOTTATT", "RECEIVED"),
    AVBRUTT("AVBRUTT", "CANCELLED");

    private final String norskStatus;

    private final String engelskStatus;

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
