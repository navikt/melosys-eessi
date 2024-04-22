package no.nav.melosys.eessi.security;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import no.nav.security.token.support.core.context.TokenValidationContext;
import no.nav.security.token.support.core.jwt.JwtToken;
import no.nav.security.token.support.spring.SpringTokenValidationContextHolder;
import org.springframework.web.context.request.RequestContextHolder;

@Slf4j
final class ContextHolder {

    private final SpringTokenValidationContextHolder context;

    private static ContextHolder instans;

    private static final String AAD = "aad";

    private ContextHolder(SpringTokenValidationContextHolder context) {
        this.context = context;
    }

    static ContextHolder getInstance() {
        if (instans == null) {
            instans = new ContextHolder(new SpringTokenValidationContextHolder());
        }
        return instans;
    }

    boolean canExchangeOBOToken() {
        return getTokenContext().getJwtToken(AAD) != null;
    }

    private TokenValidationContext getTokenContext() {
        return context.getTokenValidationContext();
    }
}
