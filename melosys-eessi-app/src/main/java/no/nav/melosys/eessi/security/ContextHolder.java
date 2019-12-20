package no.nav.melosys.eessi.security;

import java.util.Optional;

import no.nav.security.token.support.core.context.TokenValidationContext;
import no.nav.security.token.support.core.jwt.JwtToken;
import no.nav.security.token.support.spring.SpringTokenValidationContextHolder;
import org.springframework.web.context.request.RequestContextHolder;

final class ContextHolder {

    private final SpringTokenValidationContextHolder context;

    private static ContextHolder instans;

    private static final String ISSO = "isso";
    private static final String REST_STS = "reststs";

    private ContextHolder(SpringTokenValidationContextHolder context) {
        this.context = context;
    }

    static ContextHolder getInstance() {
        if (instans == null) {
            instans = new ContextHolder(new SpringTokenValidationContextHolder());
        }
        return instans;
    }

    Optional<String> getOidcToken() {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return Optional.empty();
        }

        String restStsToken = restStsToken();
        return restStsToken != null ? Optional.of(restStsToken) : Optional.ofNullable(issoToken());
    }

    private String restStsToken() {
        return jwtTokenAsString(REST_STS);
    }

    private String issoToken() {
        return jwtTokenAsString(ISSO);
    }

    private String jwtTokenAsString(String issuer) {
        JwtToken jwtToken = getTokenContext().getJwtToken(issuer);
        return jwtToken != null ? jwtToken.getTokenAsString() : null;
    }

    private TokenValidationContext getTokenContext() {
        return context.getTokenValidationContext();
    }
}
