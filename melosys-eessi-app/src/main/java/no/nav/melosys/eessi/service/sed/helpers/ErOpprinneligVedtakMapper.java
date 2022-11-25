package no.nav.melosys.eessi.service.sed.helpers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static org.hibernate.internal.util.StringHelper.isEmpty;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErOpprinneligVedtakMapper {

    public static Optional<Boolean> map(String erOpprinneligVedtak) {
        if (isEmpty(erOpprinneligVedtak)) {
            log.warn("Mangler 'eropprinneligvedtak' på vedtak");
            return Optional.empty();
        }

        if (erOpprinneligVedtak.equalsIgnoreCase("ja") || erOpprinneligVedtak.equalsIgnoreCase("true")) {
            log.info("Har registrert et opprinnelig vedtak");
            return Optional.of(true);
        } else if (erOpprinneligVedtak.equalsIgnoreCase("nei") || erOpprinneligVedtak.equalsIgnoreCase("false")) {
            log.info("Har registrert et ikke-opprinnelig vedtak");
            return Optional.of(false);
        } else {
            log.warn("Ukjent verdi 'eropprinneligvedtak' på vedtak, mottatt verdi: {}", erOpprinneligVedtak);
            return Optional.empty();
        }
    }
}
