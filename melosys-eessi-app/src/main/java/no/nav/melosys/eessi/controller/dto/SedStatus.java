package no.nav.melosys.eessi.controller.dto;

import java.util.Arrays;
import org.springframework.util.StringUtils;

public enum SedStatus {
    UTKAST("UTKAST", "NEW"),
    SENDT("SENDT", "SENT"),
    MOTTATT("MOTTATT", "RECEIVED"),
    TOM("TOM", "EMPTY"),
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

    public String getNorskStatus() {
        return norskStatus;
    }

    public String getEngelskStatus() {
        return engelskStatus;
    }
}
