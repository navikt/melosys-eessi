package no.nav.melosys.eessi.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyString;


public class HeaderValidatorRequestInterceptorTest {

    private final String HEADER_NAME = "X-HEADER";
    private final String HEADER_VALUE = "val123";

    @Test
    public void preHandle_naisProfile_expectValidationSuccessful() throws Exception {
        HeaderValidatorRequestInterceptor requestInterceptor = new HeaderValidatorRequestInterceptor(HEADER_NAME,
                HEADER_VALUE, "nais");
        Boolean result;

        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();

        result = requestInterceptor.preHandle(request, response, null);

        assertThat(result, is(Boolean.FALSE));

    }

    @Test
    public void preHandle_lovalProfile_expectValidationUnauthorized() throws Exception {
        HeaderValidatorRequestInterceptor requestInterceptor = new HeaderValidatorRequestInterceptor(HEADER_NAME,
                HEADER_VALUE, "local");
        Boolean result;

        HttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        result = requestInterceptor.preHandle(request, response, null);

        assertThat(result, is(Boolean.TRUE));
        assertThat(response.getErrorMessage(), not(isEmptyString()));
    }
}