package no.nav.melosys.eessi.security;

import java.util.Map;

import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
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
    private static final String IDTYP = "idtyp";
    private static final String APP = "app";

    private ContextHolder(SpringTokenValidationContextHolder context) {
        this.context = context;
    }

    public ContextHolder() {
        this.context = new SpringTokenValidationContextHolder();
    }

    static ContextHolder getInstance() {
        if (instans == null) {
            instans = new ContextHolder(new SpringTokenValidationContextHolder());
        }
        return instans;
    }

    boolean canExchangeOBOToken() {
        TokenValidationContext tokenValidationContext = getTokenContext();
        if (tokenValidationContext != null) {
            JwtToken jwtToken = getTokenContext().getJwtToken(AAD);

            return (jwtToken != null && !aktørErApplikasjon(jwtToken));
        }
        return false;
    }

    // Token har allerede blitt validert
    @SneakyThrows
    boolean aktørErApplikasjon(JwtToken jwtToken) {
        SignedJWT jwt = SignedJWT.parse(jwtToken.getTokenAsString());
        Map<String, Object> payload = jwt.getPayload().toJSONObject();
        return payload.containsKey(IDTYP) && payload.get(IDTYP).equals(APP);
    }

    public TokenValidationContext getTokenContext() {
        if (RequestContextHolder.getRequestAttributes() != null && context.getTokenValidationContext().hasTokenFor(AAD)) {
            return context.getTokenValidationContext();
        }
        return null;
    }
}
