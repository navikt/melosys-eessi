package no.nav.melosys.eessi.security;

import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.assertj.core.api.Assertions.assertThat;


public class HeaderValidatorRequestInterceptorTest {

    private final String HEADER_NAME = "X-HEADER";
    private final String HEADER_VALUE = "val123";

    @Test
    public void preHandle_naisProfilUtenToken_forventFeiletValidering() throws Exception {
        HeaderValidatorRequestInterceptor requestInterceptor = new HeaderValidatorRequestInterceptor(HEADER_NAME,
                HEADER_VALUE, "nais");
        boolean result;

        HttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        result = requestInterceptor.preHandle(request, response, null);

        assertThat(result).isFalse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void preHandle_localProfil_forventSuksessfullValidering() throws Exception {
        HeaderValidatorRequestInterceptor requestInterceptor = new HeaderValidatorRequestInterceptor(HEADER_NAME,
                HEADER_VALUE, "local");
        boolean result;

        HttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        result = requestInterceptor.preHandle(request, response, null);

        assertThat(result).isTrue();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}