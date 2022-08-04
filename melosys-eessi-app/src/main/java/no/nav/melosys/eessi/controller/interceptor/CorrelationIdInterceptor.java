package no.nav.melosys.eessi.controller.interceptor;

import java.util.Objects;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import static no.nav.melosys.eessi.config.MDCLogging.CORRELATION_ID;
import static no.nav.melosys.eessi.config.MDCLogging.X_CORRELATION_ID;

public class CorrelationIdInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CorrelationIdInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String correlationId = getCorrelationId(request);
        MDC.put(CORRELATION_ID, correlationId);

        LOGGER.debug("Set MDC values");
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.remove(CORRELATION_ID);
        LOGGER.debug("Cleared MDC values");
    }

    private String getCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader(X_CORRELATION_ID);
        if (isMissingCorrelationId(correlationId)) {
            return UUID.randomUUID().toString();
        }
        return correlationId;
    }

    private boolean isMissingCorrelationId(String correlationId) {
        return Objects.isNull(correlationId) || correlationId.isBlank();
    }

}
