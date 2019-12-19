package no.nav.melosys.eessi.config;

import java.util.Arrays;

import no.nav.melosys.eessi.security.HeaderValidatorRequestInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiConfig implements WebMvcConfigurer {

    private static final String API_PREFIX = "/api";

    private final Environment environment;

    public ApiConfig(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(API_PREFIX, ApiConfig::erApiTjeneste);
    }

    private static boolean erApiTjeneste(Class clazz) {
        return clazz.getPackageName().startsWith(Konstanter.CONTROLLER_PAKKE)
                && clazz.isAnnotationPresent(RestController.class);
    }
}
