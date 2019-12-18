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

    private static final String NAIS = "nais";
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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (naisProfil()) {
            registry.addInterceptor(requestInterceptor())
                    .addPathPatterns("/api/**");
        }
    }

    private HeaderValidatorRequestInterceptor requestInterceptor() {
        return new HeaderValidatorRequestInterceptor(
                environment.getProperty("melosys.api.security.header-name"),
                environment.getProperty("melosys.api.security.header-value"),
                environment.getProperty("spring.profiles.active")
        );
    }

    private boolean naisProfil() {
        return Arrays.stream(environment.getActiveProfiles()).anyMatch(NAIS::equalsIgnoreCase);
    }
}
