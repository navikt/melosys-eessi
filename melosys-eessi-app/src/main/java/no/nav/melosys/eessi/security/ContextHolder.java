package no.nav.melosys.eessi.security;

import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.token.support.core.context.TokenValidationContext;
import no.nav.security.token.support.core.jwt.JwtToken;
import no.nav.security.token.support.spring.SpringTokenValidationContextHolder;

import java.text.ParseException;

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

    boolean canExchangeOBOToken() throws ParseException {
        JwtToken jwtToken = getTokenContext().getJwtToken(AAD);
        try {
            return (jwtToken != null && harNavIdent(jwtToken));
        } catch (ParseException e) {
            log.error("Feil ved parsing av JWT token", e);
            throw e;
        }
    }

    boolean harNavIdent(JwtToken jwtToken) throws ParseException {
        SignedJWT jwt = SignedJWT.parse(jwtToken.getTokenAsString());
        return jwt.getPayload().toJSONObject().containsKey("NAVident");
    }

    private TokenValidationContext getTokenContext() {
        return context.getTokenValidationContext();
    }
}
