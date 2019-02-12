package no.nav.melosys.eessi.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class HeaderValidatorRequestInterceptor implements HandlerInterceptor {

    private final String apiHeaderName;
    private final String apiHeaderValue;
    private final String activeProfile;

    public HeaderValidatorRequestInterceptor(String apiHeaderName, String apiHeaderValue,
            String activeProfile) {
        this.apiHeaderName = apiHeaderName;
        this.apiHeaderValue = apiHeaderValue;
        this.activeProfile = activeProfile;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {

        log.debug("Intercepted.preHandle: " + "method: {} - servletPath: {}", request.getMethod(),
                request.getServletPath());

        if (!isValidApiRequest(request)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        return true;
    }

    private boolean isValidApiRequest(HttpServletRequest request) {
//        if (!activeProfile.equalsIgnoreCase("nais")) {
//            return true;
//        }
        return apiHeaderValue.equalsIgnoreCase(request.getHeader(apiHeaderName));
    }
}
